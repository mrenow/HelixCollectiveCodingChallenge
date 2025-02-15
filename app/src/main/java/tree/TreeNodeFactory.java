package tree;

import java.util.ArrayList;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import error.TreeParsingError;

public class TreeNodeFactory {

    /**
     * If the object has a single "ref" key, then try to intepret the contents as a
     * tree reference, throwing
     * an error if unsuccessful.
     * Otherwise, return empty().
     * 
     * @param jsonObj
     * @return
     * @throws TreeParsingError
     */
    public static @Nullable TreeNode tryParseRef(JSONObject jsonObj, TreeNode root) throws TreeParsingError {
        if (jsonObj.length() != 1 || !jsonObj.has("ref")) {
            return null;
        }
        try {

            String path = jsonObj.getString("ref");

            var ls = new ArrayList<String>();

            boolean absolute = path.startsWith("/");
            if (absolute) {
                path = path.substring(1);
            }
            // Remove trailing slash as java split does not handle this case nicely
            if (path.endsWith("/")) {
                path = path.substring(0, path.length() - 1);
            }
            // Path should be empty if path is root.
            if (path != "") {
                var pathParts = path.split("/");
                for (int i = 0; i < pathParts.length; i++) {
                    ls.add(pathParts[i]);
                }
            }
            if (!absolute) {
                // Resolve relative path:
                // Swap root for found node and continue as usual.
                var searchNodes = root.traverse()
                        .filter(kv -> {
                            var pathArray = kv.path().split("/");
                            var x = pathArray[pathArray.length - 1].equals(ls.get(0));
                            return x;
                        })
                        .map(kv -> kv.node())
                        .toList();
                if (searchNodes.size() != 1) {
                    throw new TreeParsingError("Invalid reference: " + path + ", " + searchNodes.toString());
                }
                root = searchNodes.get(0);
                // continue with the path excluding the searched term
                ls.remove(0);
            }
            // Resolve path
            var resolvedOrNull = root.get(ls);
            if (resolvedOrNull == null) {
                throw new TreeParsingError("Invalid reference: " + path);
            }
            if (!resolvedOrNull.complete()) {
                throw new TreeParsingError("Reference to incomplete node: " + path);
            }
            return resolvedOrNull;

        } catch (JSONException e) {
            throw new TreeParsingError(e.getMessage());
        }
    }

    /**
     * If the object has a single "seq" key, then try to intepret the contents as a
     * sequence, throwing
     * an error if unsuccessful.
     * Otherwise, return empty().
     * 
     * @param jsonObj
     * @return
     * @throws TreeParsingError
     */
    public static @Nullable TreeAtom[] tryParseSeq(JSONObject jsonObj) throws TreeParsingError {
        if (jsonObj.length() != 1 || !jsonObj.has("seq")) {
            return null;
        }
        try {
            JSONObject range = jsonObj.getJSONObject("seq");
            return IntStream.rangeClosed(range.getInt("start"), range.getInt("end"))
                    .mapToObj(x -> TreeAtom.of(x))
                    .toArray(TreeAtom[]::new);
        } catch (JSONException e) {
            throw new TreeParsingError(e.getMessage());
        }

    }

    public static Output create(JSONArray arr, TreeNode root) throws TreeParsingError {
        return new NodeOutput(new TreeStructure());
    }

    public static Output create(JSONObject jsonObj, TreeNode root) throws TreeParsingError {
        @Nullable
        TreeAtom[] outSeq;
        if ((outSeq = tryParseSeq(jsonObj)) != null) {
            return new SequenceOutput(outSeq);
        }
        @Nullable
        TreeNode outRef;
        if ((outRef = tryParseRef(jsonObj, root)) != null) {
            return new NodeOutput(outRef);
        }
        return new NodeOutput(new TreeHierarchy());
    }

    public static Output create(Object json, TreeNode root) throws TreeParsingError {
        return switch (json) {
            case JSONObject jsonObj -> create(jsonObj, root);
            case JSONArray jsonArr -> create(jsonArr, root);
            case String s -> new NodeOutput(TreeAtom.checked(s));
            default -> throw new TreeParsingError("Invalid object type");
        };
    }

    final record SequenceOutput(TreeNode[] seq) implements Output {
    }

    final record NodeOutput(TreeNode node) implements Output {
    }

    sealed interface Output permits SequenceOutput, NodeOutput {
    }

}

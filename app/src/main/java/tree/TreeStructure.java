package tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import error.TreeParsingError;

public final class TreeStructure implements TreeNode {
    List<TreeNode> children;
    boolean complete = false;

    TreeStructure() {
        this.children = new ArrayList<>();
    }

    public TreeStructure(List<TreeNode> children) {
        this.children = children;
        this.complete = true;
    }

    public static TreeStructure of(String json) throws TreeParsingError {
		try {
            var jsonArray = new JSONArray(json);
            return of(jsonArray);
        } catch (JSONException e) {
            throw new TreeParsingError("Invalid JSON: " + e.getMessage());
        }
	}
 
    public static TreeStructure of(Object json) throws TreeParsingError {
		var newObj = new TreeStructure();
		newObj._postInit(json, newObj);
		return newObj;
	}
 

    @Override
    public boolean complete() {
        return this.complete;
    }

    @Override
    public TreeNode get(String key) {
        try {
            int index = Integer.parseInt(key);
            return children.get(index);
        } catch (NumberFormatException e) {
            return null;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Override
    public String[] keys() {
        return IntStream.range(0, children.size()).mapToObj(Integer::toString).toArray(String[]::new);
    }

    @Override
    public void _postInit(Object json, TreeNode root) throws TreeParsingError {
        if (complete) {
			return;
		}
        switch (json) {
            case JSONArray jsonObj -> {
                for (int key = 0; key < jsonObj.length(); key++) {
                    var childJson = jsonObj.get(key);
                    TreeNodeFactory.Output out = TreeNodeFactory.create(childJson, root);
                    switch (out) {
                        case TreeNodeFactory.SequenceOutput seqOut -> {
                            children.addAll(List.of(seqOut.seq()));
                        }
                        case TreeNodeFactory.NodeOutput nodeOut -> {
                            children.add(nodeOut.node());
                            nodeOut.node()._postInit(childJson, root);
                        }
                    }
                }
            }
            case Object jsonArray -> {
                throw new IllegalArgumentException();
            }
        }
        this.complete = true;
    }

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}
		if (obj instanceof TreeStructure other) {
			var s1 = new HashMap<String, TreeAtom>();
			var s2 = new HashMap<String, TreeAtom>();
			s1.putAll(this.traverseLeaves().collect(Collectors.<PathAndNode, String, TreeAtom>toMap(kv->kv.path(),kv->(TreeAtom)kv.node())));
			s2.putAll(other.traverseLeaves().collect(Collectors.<PathAndNode, String, TreeAtom>toMap(kv->kv.path(),kv->(TreeAtom)kv.node())));
			return s1.equals(s2) && this.complete == other.complete;
		}
		return false;
	}
    @Override
    public String toString() {
        return "[" + children.stream().map(TreeNode::toString).collect(Collectors.joining(",")) + "]";
    } 


}
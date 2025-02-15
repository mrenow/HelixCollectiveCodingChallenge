package tree;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import error.TreeParsingError;

/**
 * Represents an element of a tree structure which can be serialized to a json
 * string.
 * 
 */
public sealed interface TreeNode permits TreeAtom, TreeHierarchy, TreeStructure {

    /**
     * Package private method
     * 
     * @param object
     * @param root
     * @throws TreeParsingError
     */
    default void _postInit(Object object, TreeNode root) throws TreeParsingError {
        return;
    }

    /**
     * @return Whether this node has been finished construction
     */
    default boolean complete() {
        return true;
    }

    /**
     * @param key
     * @return child node with the given key
     */
    @Nullable
    TreeNode get(String key);

    /**
     * Guaranteed that the get(key) will return a non-null value for each key in
     * keys()
     * 
     * @return keys of the children of this node
     */
    String[] keys();

    /**
     * Returns null if the path does not exist.
     * 
     * @param key child node with the given path.
     * @return 
     */
    default @Nullable TreeNode get(Iterable<String> path) {
        TreeNode optNode = this;
        for (String key : path) {
            if (optNode == null) {
                return null;
            } else {
                optNode = optNode.get(key);
            }
        }
        return optNode;
    }

    /**
     * @return Traversal of all tree paths and nodes
     */
    public default Stream<PathAndNode> traverse() {
        return Stream.concat(
                Stream.of(new PathAndNode("", this)),
                Stream.of(keys()).flatMap(key -> {
                    @Nullable
                    TreeNode child = get(key);
                    if (child == null) {
                        return Stream.empty(); // Really this should not happen if keys() is correctly implemented.
                    }
                    return child.traverse().map(p -> new PathAndNode(key + "/" + p.path(), p.node()));
                }));
    }

    /**
     * @return Traversal of all tree leaves
     */
    public default Stream<PathAndNode> traverseLeaves() {
        return Stream.of(keys()).flatMap(key -> {
            @Nullable
            TreeNode child = get(key);
            if (child == null) {
                return Stream.empty(); // Really this should not happen if keys() is correctly implemented.
            }
            return child.traverseLeaves().map(p -> new PathAndNode(key + "/" + p.path(), p.node()));
        });
    }
}
package helixcodingchallenge;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import tree.TreeAtom;
import tree.TreeHierarchy;
import tree.TreeNode;
import tree.TreeStructure;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TraversalTest {
    @Test
    void testParseTreeSingle() {

        TreeStructure tree = new TreeStructure(List.<TreeNode>of(
                new TreeAtom("A")));

        assertIterableEquals(tree.traverse().map(kv -> kv.path()).toList(), List.<String>of(
                "", "0/"));
        assertIterableEquals(tree.traverseLeaves().map(kv -> kv.path()).toList(), List.<String>of(
                "0/"));
    }

    @Test
    void testParseTreeRecurse() {
        TreeStructure tree = new TreeStructure(List.<TreeNode>of(
                new TreeStructure(List.<TreeNode>of(
                        new TreeHierarchy(Map.<String, TreeNode>of(
                                "'1'", new TreeStructure(List.<TreeNode>of(
                                        new TreeAtom("A")))))))));

        assertIterableEquals(tree.traverse().map(kv -> kv.path()).toList(), List.<String>of(
                "",
                "0/",
                "0/0/",
                "0/0/'1'/",
                "0/0/'1'/0/"));
        assertIterableEquals(tree.traverseLeaves().map(kv -> kv.path()).toList(), List.<String>of(
                "0/0/'1'/0/"));
    }

    @Test
    void testParseTreeRecurse2() {
        LinkedHashMap<String, TreeNode> hierarchy = new LinkedHashMap<>();
        hierarchy.put("'1'", new TreeStructure(List.<TreeNode>of(
                new TreeAtom("'2'"),
                new TreeAtom("'3'"))));
        hierarchy.put("'2'", new TreeAtom("Hello"));
        hierarchy.put("'3'", new TreeAtom("World"));

        TreeStructure tree = new TreeStructure(List.<TreeNode>of(
                new TreeAtom("A"),
                new TreeAtom("A"),
                new TreeHierarchy(hierarchy),
                new TreeStructure(List.<TreeNode>of(
                        new TreeAtom("B"),
                        new TreeAtom("A"))),
                new TreeAtom("'1'")));

        assertIterableEquals(tree.traverse().map(kv -> kv.path()).toList(), List.<String>of(
                "",
                "0/",
                "1/",
                "2/",
                "2/'1'/",
                "2/'1'/0/",
                "2/'1'/1/",
                "2/'2'/",
                "2/'3'/",
                "3/",
                "3/0/",
                "3/1/",
                "4/"));
        assertIterableEquals(tree.traverseLeaves().map(kv -> kv.path()).toList(), List.<String>of(
                "0/",
                "1/",
                "2/'1'/0/",
                "2/'1'/1/",
                "2/'2'/",
                "2/'3'/",
                "3/0/",
                "3/1/",
                "4/"));
    }
}

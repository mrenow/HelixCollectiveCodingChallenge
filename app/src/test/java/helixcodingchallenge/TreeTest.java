package helixcodingchallenge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import org.junit.jupiter.api.Test;

import error.TreeParsingError;
import tree.PathAndNode;
import tree.TreeAtom;
import tree.TreeStructure;

import java.util.List;

public class TreeTest {
    @Test
    void testParseTreeSpecExample() throws TreeParsingError {
        TreeStructure tree = TreeStructure.of("[\n" + //
                "  \"A\",\n" + //
                "  {\"ref\": \"/0\"},\n" + //
                "  { \"seq\": { \"start\": 1, \"end\": 3 } },\n" + //
                "  [\n" + //
                "    \"B\",\n" + //
                "    {\"ref\": \"/0\"}\n" + //
                "  ],\n" + //
                "  {\"ref\": \"/2\"}\n" + //
                "]");
        assertIterableEquals(tree.traverse().toList(), List.of(new PathAndNode[] {
                new PathAndNode("", tree),
                new PathAndNode("0/", new TreeAtom("A")),
                new PathAndNode("1/", new TreeAtom("A")),
                new PathAndNode("2/", TreeAtom.of(1)),
                new PathAndNode("3/", TreeAtom.of(2)),
                new PathAndNode("4/", TreeAtom.of(3)),
                new PathAndNode("5/", new TreeStructure(List.of(
                        new TreeAtom("B"),
                        new TreeAtom("A")))),
                new PathAndNode("5/0/", new TreeAtom("B")),
                new PathAndNode("5/1/", new TreeAtom("A")),
                new PathAndNode("6/", TreeAtom.of(1)),

        }));
    }

    @Test
    void testParseTreeString() throws TreeParsingError {
        TreeStructure tree = TreeStructure.of("[\n" + //
                "  \"A\",\n" + //
                "  {\"ref\": \"/0\"},\n" + //
                "  { \"seq\": { \"start\": 1, \"end\": 3 } },\n" + //
                "  [\n" + //
                "    \"B\",\n" + //
                "    {\"ref\": \"/0\"}\n" + //
                "  ],\n" + //
                "  {\"ref\": \"/2\"}\n" + //
                "]");
        assertEquals(tree.toString(),  "[\"A\",\"A\",\"'1'\",\"'2'\",\"'3'\",[\"B\",\"A\"],\"'1'\"]");
    }
    @Test
    void testParseTreeHeirarchyAndRelativeRefString() throws TreeParsingError {
        TreeStructure tree = TreeStructure.of("[\n" + //
                        "  [\n" + //
                        "    {\n" + //
                        "\t    \"c\":   { \"seq\": { \"start\": -1, \"end\": 0 } },\n" + //
                        "       \"d\": {\"ref\": \"c\"},\n" + //
                        "       \"e\": {\"ref\": \"d/1\"}\n" + //
                        "\t},\n" + //
                        "    \"B\",\n" + //
                        "  ],\n" + //
                        "  {\"ref\": \"/0\"},\n" + //
                        "  {\"ref\": \"/0/1\"}\n" + //
                        "]");
        assertEquals(tree.toString(),  "[[{\"c\":[\"'-1'\",\"'0'\"],\"d\":[\"'-1'\",\"'0'\"],\"e\":\"'0'\"},\"B\"],[{\"c\":[\"'-1'\",\"'0'\"],\"d\":[\"'-1'\",\"'0'\"],\"e\":\"'0'\"},\"B\"],\"B\"]");
    }
}

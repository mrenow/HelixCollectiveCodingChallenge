package helixcodingchallenge;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

public class SplitTest {
    @Test
    void testSplit() {
        // Wtf is this silly behaviour
        assertArrayEquals("a/".split("/"), new String[] {"a"});
        assertArrayEquals("/a".split("/"), new String[] {"", "a"}); // Like actually
        assertArrayEquals("/a/".split("/"), new String[] {"", "a"});
        assertArrayEquals("///".split("/"), new String[] {});
        assertArrayEquals("//".split("/"), new String[] {});
        assertArrayEquals("/".split("/"), new String[] {});
        assertArrayEquals("a//".split("/"), new String[] {"a"});
        assertArrayEquals("".split("/"), new String[] {""});
    }
}

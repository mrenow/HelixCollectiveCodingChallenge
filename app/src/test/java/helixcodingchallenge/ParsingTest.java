package helixcodingchallenge;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import tree.Parsing;

public class ParsingTest {
    @Test
    void testQuotedNumbersCheck() {

        String quotedNumbers[] = new String[] {
                "'123'",
                "'-1'",
                "'+0'",
                "'-0'",
                "'0'",
                "'123.9'",
                "'-13.151'",
                "'2.3123123e143'",
                "'-1E-134'",
                "'+0.000181'",
                "'-.1'"
        };

        String notQuotedNumbers[] = new String[] {
                "'12s3'",
                "'-1a'",
                "'1-1'",
                "'.8.'",
                "'2.3.123123e143'",
                "'-1e.134'",
                "'1ee3'",
                "'",
                "0'"
        };

        for (String number : quotedNumbers) {
            assertTrue(Parsing.isQuotedNumber(number), () -> number);
        }
        for (String number : notQuotedNumbers) {
            assertTrue(!Parsing.isQuotedNumber(number), () -> number);
        }

    }

    @Test
    void testIdentifierCheck() {

        String identifiers[] = new String[] {
                "a",
                "abc1",
        };

        String notIdentifiers[] = new String[] {
                "1bac",
                "22",
        };

        for (String identifier : identifiers) {
            assertTrue(Parsing.isValidIdentifier(identifier));
        }
        for (String identifier : notIdentifiers) {
            assertTrue(!Parsing.isValidIdentifier(identifier));
        }

    }

    @Test
    void testPathCheck() {

        String paths[] = new String[] {
                "a",
                "a/a",
                "/0/a0",
                "/0/'0'",
        };

        String notPaths[] = new String[] {
                "//",
                "",
                "/0/'a0'",
        };

        for (String path : paths) {
            assertTrue(Parsing.isValidPath(path));
        }

        for (String path : notPaths) {
            assertTrue(!Parsing.isValidPath(path));
        }

    }

}

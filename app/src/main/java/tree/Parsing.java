package tree;

import java.util.regex.Pattern;

public class Parsing {

    final static Pattern identifierPattern = Pattern.compile("[a-zA-Z][a-zA-Z0-9]*");
    final static Pattern quotedNumberPattern = Pattern.compile("'[-+]?(\\d*[.])?\\d+([eE][-+]?\\d+)?'");
    final static Pattern pathPattern = Pattern.compile("/?(0|[1-9]\\d*|[a-zA-Z][a-zA-Z0-9]*|'[-+]?(\\d*[.])?\\d+([eE][-+]?\\d+)?')(/(0|[1-9]\\d*|[a-zA-Z][a-zA-Z0-9]*|'[-+]?(\\d*[.])?\\d+([eE][-+]?\\d+)?'))*");

    public static boolean isValidIdentifier(String identifier) {
        return identifierPattern.matcher(identifier).matches();
    }
    public static boolean isQuotedNumber(String numberString) {
        return quotedNumberPattern.matcher(numberString).matches();
    }

    public static boolean isValidPath(String path) {
        return pathPattern.matcher(path).matches();
    }
}

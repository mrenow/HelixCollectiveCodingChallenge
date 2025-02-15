package error;

public class TreeParsingError extends Exception {
    public TreeParsingError(String value){
        super(value);
    }
    public TreeParsingError() {
        super();
    }
}

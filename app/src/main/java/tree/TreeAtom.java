package tree;

import java.util.stream.Stream;

import javax.annotation.Nullable;

import error.TreeParsingError;

enum TreeAtomVariant {
	Identifier,
	QuotedNumber
}


public final record TreeAtom(String value) implements TreeNode {
	
	public static TreeAtom of(int value) {
		return new TreeAtom("'" + Integer.toString(value) + "'");
	}

	public static TreeAtom checked(String value) throws TreeParsingError {
		if (_compute_type(value) == null) {
			throw new TreeParsingError("Value cannot be null");
		}
		return new TreeAtom(value);
	}

	private static @Nullable TreeAtomVariant _compute_type(String value) {
		// Check valid identifier
		if (Parsing.isQuotedNumber(value)) {
			return TreeAtomVariant.QuotedNumber;
		} else if (Parsing.isValidIdentifier(value)) {
			return TreeAtomVariant.Identifier;
		} else {
			return null;
		}
	}



	@Override
	public TreeNode get(String key) {
		return null;
	}

	@Override
	public String[] keys() {
		return new String[0];
	}

	@Override
	public Stream<PathAndNode> traverseLeaves(){
		return Stream.of(new PathAndNode("", this));
	}
	@Override
	public final String toString() {
		return "\"" + value + "\"";
	}
}

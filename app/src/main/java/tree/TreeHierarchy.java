package tree;

import javax.annotation.Nullable;

import org.json.JSONObject;

import error.TreeParsingError;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class TreeHierarchy implements TreeNode {
	LinkedHashMap<String, TreeNode> heirarchy; // LinkedHashMap to preserve order
	boolean complete = false;

	TreeHierarchy() {
		this.heirarchy = new LinkedHashMap<>();
	}

	public TreeHierarchy(Map<String, TreeNode> heirarchy) {
		this.heirarchy = new LinkedHashMap<>(heirarchy);
		this.complete = true;
	}

	public static TreeHierarchy of(String json) throws TreeParsingError {
		try {
			var jsonObj = new JSONObject(json);
			return of(jsonObj);
		} catch (Exception e) {
			throw new TreeParsingError("Invalid JSON: " + e.getMessage());
		}
	}

	public static TreeHierarchy of(Object json) throws TreeParsingError {
		var newObj = new TreeHierarchy();
		newObj._postInit(json, newObj);
		return newObj;
	}

	public boolean complete() {
		return complete;
	}

	/**
	 * Post construction intialization of this node
	 * Required because in order for refernce resolution to work,
	 * between construction and resolution, this node may need to be added to the
	 * parent.
	 */
	@Override
	public void _postInit(Object json, TreeNode root) throws TreeParsingError {
		// for each [key], node in json:
		// NodeFactoryOutput out = NodeFactory.create(json, root)
		// if out is Sequence:
		// handle dependent on list
		// else:
		// self.data.add([key], out.node)
		// node.post_initialize(data, root) # only required for objects that need ref
		// self.complete = True
		// return
		if (complete) {
			return;
		}
		switch (json) {
			case JSONObject jsonObj -> {
				for (var key : jsonObj.keySet()) {
					var childJson = jsonObj.get(key);
					TreeNodeFactory.Output out = TreeNodeFactory.create(childJson, root);
					switch (out) {
						case TreeNodeFactory.SequenceOutput seqOut -> {
							heirarchy.put(key, new TreeStructure(List.of(seqOut.seq())));
						}
						case TreeNodeFactory.NodeOutput nodeOut -> {
							heirarchy.put(key, nodeOut.node());
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
	public @Nullable TreeNode get(String key) {
		return heirarchy.get(key);
	}

	@Override
	public String[] keys() {
		return heirarchy.keySet().toArray(String[]::new);
	}

	@Override
	public boolean equals(Object obj) {
		if (super.equals(obj)) {
			return true;
		}
		if (obj instanceof TreeHierarchy other) {
			var s1 = new HashMap<String, TreeAtom>();
			var s2 = new HashMap<String, TreeAtom>();
			s1.putAll(this.traverseLeaves().collect(
					Collectors.<PathAndNode, String, TreeAtom>toMap(kv -> kv.path(), kv -> (TreeAtom) kv.node())));
			s2.putAll(other.traverseLeaves().collect(
					Collectors.<PathAndNode, String, TreeAtom>toMap(kv -> kv.path(), kv -> (TreeAtom) kv.node())));
			return s1.equals(s2) && this.complete == other.complete;
		}
		return false;
	}

	@Override
	public String toString() {
		// return a json-like format
		return heirarchy.entrySet().stream().map(kv -> "\"" +  kv.getKey() + "\"" + ":" + kv.getValue().toString())
				.collect(Collectors.joining(",", "{", "}"));

	}
}

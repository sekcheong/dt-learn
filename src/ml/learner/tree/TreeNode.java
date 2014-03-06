package ml.learner.tree;

import ml.data.Instance;
import ml.data.features.Feature;
import ml.data.features.Value;

public class TreeNode {
	private TreeNode[]_childNodes = null;
	private Value _outcome = null;
	private TreeNode _parent = null;
	private SplitPoint _split =null;
	private int[] _outcomeCounts;
	
	public TreeNode() {
		
	}
	
	public TreeNode(Value outcome) {
		this.makeLeaf(outcome);
	}

	public TreeNode[] childNodes() {
		return _childNodes;
	}
	
	public TreeNode parent() {
		return _parent;
	}
	
	public void setSplit(SplitPoint split) {
		_split = split;
	}
	
	public TreeNode child(Instance d) {
		Value v = d.values( _split.feature().index());
		return child(v);
	}
	
	public TreeNode child(Value value) {
		if (_childNodes == null || _childNodes.length==0) return null;
		if (value==null) return null;
		return _childNodes[_split.childNode(value)];
	}
	
	public boolean reachable(Instance d) {
		TreeNode r =this;
		
		// find the root node
		while (r.parent()!=null) r = r.parent();
		
		while (r!=null) {
			if (r==this) return true;
			r = r.child(d);
			if (r.isLeaf()) return false;
		}
		return false;
	}
	
	public boolean isLeaf() {
		return _childNodes==null;
	}
	
	public Value classify(Instance instance) {
		TreeNode n = this;
		while ((n!=null && !n.isLeaf())) {
			n = n.child(instance);
		}
		if (n!=null) return n.outcome();
		return null;
	}
	
	public Feature feature() {
		return _split.feature();
	}
	
	public void makeLeaf(Value outcome) {
		_outcome = outcome;
		_split=null;
		_childNodes=null; 
	}
	
	public Value outcome() {
		return _outcome;
	}
	
	public String name() {
		if (_split != null) {
			return _split.feature().name();
		}
		return null;
	}
	
	public String childNodeName(int index) {
		return _split.childName(index);
	}

	public void setChild(int index, TreeNode child) {
		if (child==null) return;
		if (_childNodes==null) {
			_childNodes = new TreeNode[_split.childNodeCount()];
		}
		_childNodes[index] = child;
		child._parent=this;
		
	}

	public void setOutcomeCounts(int[] counts) {
		_outcomeCounts = counts;
	}
	
	public int[] outcomeCounts() {
		return _outcomeCounts;
	}

	public String splitName(int i) {
		return _split.childName(i);
	}
}

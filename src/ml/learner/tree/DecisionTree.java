package ml.learner.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import ml.data.*;
import ml.data.features.*;

public class DecisionTree {

	private TreeNode _root;

	public void train(DataSet dataSet, int minReachCount) {
		List<Feature> features = new ArrayList<Feature>();
		List<Instance> examples = new ArrayList<Instance>();
		
		for (Feature a : dataSet.features()) {
			features.add(a);
		}
		for (Instance i : dataSet.instances()) {
			examples.add(i);
		}
		_root = buildTreeID3(examples, features, dataSet.target(), null);
		pruneTree(examples, _root, minReachCount);
	}

	public double test(DataSet test) {
		return this.test(test, false);
	}
	
	public double test(DataSet test, boolean printResults) {
		Value predicted;
		int c = 0;
		
		for (Instance d : test.instances()) {
			predicted = _root.classify(d);
			Value actual = d.values(test.features().targetFeature());
			if (predicted.equals(actual)) {
				c++;
			}
			if (!printResults) continue;
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < d.length(); i++) {
				//sb.append(test.features().features(i).name()).append(":");
				sb.append(d.values(i)).append(" ");
			}
			sb.append(predicted).append(" ");
			sb.append(actual).append("\n");
			System.out.printf(sb.toString());
		}
		
		if (printResults) System.out.printf("%d %d\n", c, test.instances().count());
		
		//return the accuracy
		return (double) c / test.instances().count();
	}

	public Value classify(Instance instance) {
		return _root.classify(instance);
	}

	private TreeNode buildTreeID3(List<Instance> examples, List<Feature> features, Feature target, Value plurality) {

		if (examples == null || examples.size() == 0) return new TreeNode(plurality);

		// if all examples have the same outcome, return a leaf node with the
		// outcome
		Value outcome = hasSameOutcome(examples, target);
		if (outcome != null) return new TreeNode(outcome);

		// if no feature left to split on, return a leaf node with the plurality
		// value of the examples
		plurality = findPluralityOutcome(examples, target);
		if (features.size() == 0) return new TreeNode(plurality);

		List<SplitPoint> splits = findCandidateSplits(examples, features, target);

		// if no split candidates, return a leaf node with the plurality value
		// of the examples
		if (splits.size() == 0) return new TreeNode(plurality);

		SplitPoint s = findBestSplit(examples, target, splits);

		TreeNode root = new TreeNode();

		// if no positive information gain, create a leave node with plurality
		// value of examples
		if ((s.infoGain() <= 0)) {
			root.makeLeaf(plurality);
			return root;
		}

		// label the node with the split
		root.setSplit(s);

		// partition the training set into subsets based on the split
		List<Instance>[] p = partitionInstances(examples, s);

		// for each partition create a child node recursively and attach it to
		// the root
		for (int i = 0; i < p.length; i++) {
			// printOutcomes( p[i], target, s);
			TreeNode child = buildTreeID3(p[i], features, target, plurality);
			calcOutcomes(p[i], child, target);
			root.setChild(i, child);
		}

		return root;
	}

	private void pruneTree(List<Instance> examples, TreeNode tree, int minReach) {
		if (tree == null || tree.isLeaf()) return;
		int reachCnt = 0;
		for (Instance d : examples) {
			if (tree.reachable(d)) {
				reachCnt++;
				if (reachCnt >= minReach) {
					break;
				}
			}
		}
		if (reachCnt < minReach) {
			Value v = findPluralityOutcome(tree);
			tree.makeLeaf(v);
			return;
		}
		for (TreeNode c : tree.childNodes()) {
			pruneTree(examples, c, minReach);
		}
	}

	private Value findPluralityOutcome(TreeNode tree) {
		if (tree.isLeaf()) return tree.outcome();
		HashMap<Value, Integer> cnts = new HashMap<Value, Integer>();
		for (TreeNode c : tree.childNodes()) {
			Value v = findPluralityOutcome(c);
			Integer cnt;
			cnt = cnts.get(v);
			if (cnt == null) {
				cnt = new Integer(0);
				cnts.put(v, cnt);
			}
			cnt++;
		}
		int max = -1;
		Value pv = null;
		for (Value v : cnts.keySet()) {
			if (cnts.get(v) > max) {
				max = cnts.get(v);
				pv = v;
			}
		}
		return pv;

	}

	private void calcOutcomes(List<Instance> d, TreeNode n, Feature target) {
		int counts[] = new int[((DiscreteFeature) target).count()];
		if (d != null) {
			for (Instance i : d) {
				counts[((DiscreteValue) i.values(target)).discreteValue()]++;
			}
		}
		n.setOutcomeCounts(counts);
		return;

	}

	@SuppressWarnings("unused")
	private void printOutcomes(List<Instance> d, Feature target, SplitPoint s) {
		int counts[] = new int[((DiscreteFeature) target).count()];
		if (d != null) {
			for (Instance i : d) {
				counts[((DiscreteValue) i.values(target)).discreteValue()]++;
			}
		}
		System.out.printf("%s [%d %d]\n", s.feature().name(), counts[0], counts[1]);
	}

	private SplitPoint findBestSplit(List<Instance> examples, Feature target, List<SplitPoint> splits) {
		// calculate the entropy for the entire training set
		double e = computeEntropy(examples, target);
		int size = examples.size();
		for (SplitPoint s : splits) {
			List<Instance> p[] = partitionInstances(examples, s);
			double ce = 0;
			for (List<Instance> v : p) {
				if (v == null) continue;
				ce = ce + (((double) v.size()) / size) * computeEntropy(v, target);
			}
			s.setInfoGain(e - ce);
		}
		// get the best split based on information gains
		return Collections.min(splits);
	}

	private double computeEntropy(List<Instance> d, Feature target) {
		DiscreteFeature f = (DiscreteFeature) target;
		int[] freq = new int[f.count()];

		for (Instance i : d) {
			freq[((DiscreteValue) i.values(target)).discreteValue()]++;
		}

		double e = 0;
		for (int c : freq) {
			double p = ((double) c) / d.size();
			e = e + entropy(p);
		}
		return e;
	}

	private double entropy(double p) {
		if (p == 0) return 0;
		return -p * (Math.log(p) / Math.log(2));
	}

	private Value hasSameOutcome(List<Instance> examples, Feature target) {
		if (examples == null || examples.size() == 0) return null;
		Value c = examples.get(0).values(target);
		for (Instance i : examples) {
			if (i.values(target).compareTo(c) != 0) return null;
		}
		return c;
	}

	private Value findPluralityOutcome(List<Instance> examples, Feature target) {
		int n = 0, p = 0;
		Value v = null;
		for (Instance i : examples) {
			DiscreteValue d = (DiscreteValue) i.values(target);
			if (d.discreteValue() == 0) {
				n++;
				if (n > p) v = d;
			}
			else {
				p++;
				if (p > n) v = d;
			}
		}
		return v;
	}

	private List<Instance>[] partitionInstances(List<Instance> examples, SplitPoint split) {
		// create a new partition for each feature
		@SuppressWarnings("unchecked")
		List<Instance>[] p = new ArrayList[split.childNodeCount()];
		// add instances into the partitions
		for (Instance d : examples) {
			int childIndex = split.childNode(d.values(split.feature()));
			List<Instance> l = p[childIndex];
			if (l == null) {
				l = new ArrayList<Instance>();
				p[childIndex] = l;
			}
			l.add(d);
		}
		return p;
	}

	private List<SplitPoint> findCandidateSplits(List<Instance> examples, List<Feature> features, Feature target) {
		List<SplitPoint> splits = new ArrayList<SplitPoint>();
		for (Feature f : features) {
			if (f == target) {
				continue;
			}
			if (f.dataType() == Feature.DataType.DISCRETE) {
				splits.add(new SplitPoint(f));
			}
			else {
				findNumericCandidateSplits(examples, f, target, splits);
			}
		}
		return splits;
	}

	private void findNumericCandidateSplits(List<Instance> examples, Feature feature, Feature target, List<SplitPoint> splits) {

		if (examples.size() < 2) return;

		List<Instance> d = new ArrayList<Instance>();
		// partition the data into sets where each set have instances with same
		// value for split feature
		for (Instance i : examples)
			d.add(i);
		Collections.sort(d, new SortByFeature(feature));

		// the first element in int[] is the start of the partition, the second
		// element is the count
		List<int[]> s = new ArrayList<int[]>();

		int[] p = { 0, 1 };
		s.add(p);

		for (int i = 1; i < d.size(); i++) {
			Value v1 = d.get(i).values(feature);
			Value v2 = d.get(i - 1).values(feature);
			if (v1.equals(v2)) {
				p = s.get(s.size() - 1);
				p[1]++;
			}
			else {
				p = new int[2];
				p[0] = i;
				p[1] = 1;
				s.add(p);
			}
		}

		// fewer than 2 partitions, there is nothing to split for
		if (s.size() < 2) return;

		// compute the midpoint between two adjacent sets in S
		double midPoint;
		for (int i = 0; i < s.size() - 1; i++) {
			midPoint = computeMidPoint(d, s.get(i), s.get(i + 1), feature, target);
			if (!Double.isNaN(midPoint)) {
				splits.add(new SplitPoint(feature, midPoint));
			}
		}
	}

	private double computeMidPoint(List<Instance> s, int[] a, int[] b, Feature split, Feature target) {
		Value va, vb;
		for (int i = a[0]; i < (a[0] + a[1]); i++) {
			va = s.get(i).values(target);
			for (int j = b[0]; j < (b[0] + b[1]); j++) {
				vb = (Value) s.get(j).values(target);
				if (!va.equals(vb)) {
					double p1 = (Double) s.get(i).values(split).value();
					double p2 = (Double) s.get(j).values(split).value();
					return (p1 + p2) / 2;
				}
			}
		}
		return Double.NaN;
	}

	private void printTree(StringBuilder s, TreeNode n, String indent) {
		if (n == null) return;

		if (n.isLeaf()) {
			s.append(": ").append(n.outcome()).append("\n");
		}
		else {
			for (int i = 0; i < n.childNodes().length; i++) {
				s.append(indent);
				s.append(n.splitName(i)).append(" [");
				for (int outCnt : n.childNodes()[i].outcomeCounts()) {
					s.append(outCnt).append(" ");
				}
				s.setLength(s.length() - 1);
				s.append("]");
				if (!n.childNodes()[i].isLeaf()) s.append("\n");
				printTree(s, n.childNodes()[i], indent + "|      ");
			}
		}

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		printTree(sb, _root, "");
		return sb.toString();
	}

}
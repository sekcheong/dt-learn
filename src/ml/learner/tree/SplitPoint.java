package ml.learner.tree;

import ml.data.Instance;
import ml.data.features.*;


public class SplitPoint implements Comparable<SplitPoint> {
	private Feature _feature;
	private double _threshold = Double.NaN;
	private double _infoGain = 0;
			
	public SplitPoint(Feature feature) {
		_feature = feature;
	}
	
	public SplitPoint(Feature feature, double threshold) {
		_feature = feature;
		_threshold = threshold;
	}

	public int childNodeCount() {
		if (this.isDiscrete()) {
			return ((DiscreteFeature) _feature).count();
		}
		else if (this.isNumeric()) {
			return 2;
		}
		return -1;
	}
	
	public int childNode(Instance d) {
		return childNode(d.values(_feature));
	}
	
	public int childNode(Value v) {
		if (this.isDiscrete()) {
			DiscreteValue c = (DiscreteValue) v;
			return c.discreteValue();
		}
		else if (this.isNumeric()) {
			double r = ((NumericValue) v).numericValue();
			if (r<=this.threshold()) return 0;
			else return 1;
		}
		return -1;
	}
	
	public String childName(int index) {
		if (this.isDiscrete()) {
			return _feature.name() + " = " +((DiscreteFeature) _feature).nameAt(index);
		}
		else if (this.isNumeric()) {
			if (index==0) return String.format("%s <= %.6f", _feature.name(), _threshold);
			else return String.format("%s > %.6f", _feature.name(), _threshold);
		}
		return null;
	}
	
	public boolean isNumeric() {
		return _feature.dataType()==Feature.DataType.NUMERIC;
	}
	
	public boolean isDiscrete() {
		return _feature.dataType()==Feature.DataType.DISCRETE;
	}
	
	public Feature feature() {
		return _feature;
	}
	
	public void setThreshold(double threshold) {
		_threshold = threshold;
	}
	
	public double threshold() {
		return _threshold;
	}
	
	public void setInfoGain(double gain) {
		_infoGain = gain;
	}
	
	public double infoGain() {
		return _infoGain;
	}
	
	@Override
	public int compareTo(SplitPoint o) {
		if (_infoGain==o.infoGain()) return 0;
		//sort in reverse order
		if (_infoGain<o.infoGain()) return 1;
		return -1;
	}
	
}

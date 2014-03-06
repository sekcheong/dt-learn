package ml.data.features;

import java.util.ArrayList;
import java.util.Iterator;

public class Features implements Iterable<Feature>{
	private ArrayList<Feature> _features = new ArrayList<Feature>();
	private int _targetFeatureIndex = -1;
	
	public void add(Feature feature) {
		feature.setIndex(_features.size());
		_features.add(feature);
	}
	
	public int count() {
		return _features.size();
	}
	
	public Feature features(int index) {
		return _features.get(index);
	}
	
	public void setTargetFeature(int index) throws Exception {
		_targetFeatureIndex = index;
	}
	
	public int targetFeatureIndex() {
		return _targetFeatureIndex;
	}
	
	public Feature targetFeature() {
		return this.features(this.targetFeatureIndex());
	}

	@Override
	public Iterator<Feature> iterator() {
		return _features.iterator();
	}
}

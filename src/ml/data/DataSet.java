package ml.data;

import ml.data.features.Feature;
import ml.data.features.Features;

public class DataSet {
	private String _relation;
	private Features _features;
	private Instances _instances;
	
	public DataSet() {
		_features = new Features();
		_instances = new Instances();
	}
	
	public DataSet(Features features, Instances examples) {
		_features = features;
		_instances = examples;
	}

	public Instances instances() {
		return _instances;
	}
	
	public Features features() {
		return _features;
	}
	
	public void setRelation(String rel) {
		_relation = rel;
	}
	
	public void setTarget(Feature target) throws Exception {
		_features.setTargetFeature(target.index());
	}
	
	public Feature target() {
		return _features.targetFeature();
	}
	
	public String relation() {
		return _relation;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("@relation ").append(this.relation()).append("\n");
		for (ml.data.features.Feature a:this.features()) {
			sb.append(a.toString()).append("\n");
		}
		sb.append("@data\n");
		for (Instance i:this.instances()) {
			sb.append(i.toString()).append("\n");
		}
		return sb.toString();
	}
}

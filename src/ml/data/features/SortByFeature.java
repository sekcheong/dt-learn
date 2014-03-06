package ml.data.features;

import java.util.Comparator;

import ml.data.Instance;

public class SortByFeature  implements Comparator<Instance>  {

	private Feature _feature;
	public SortByFeature(Feature feature) {
		_feature = feature;
	}
	@Override
	public int compare(Instance a, Instance b) {
		return a.values(_feature).compareTo(b.values(_feature));
	}
}

package ml.data.features;

public class NumericFeature extends Feature {
	
	
	public NumericFeature(String name) {
		this.setName(name);
		this.setDataType(Feature.DataType.NUMERIC);
	}
	
	public String toString() {
		return "@attribute '" + this.name() + "' numeric";
	}

}

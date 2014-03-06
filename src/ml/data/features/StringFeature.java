package ml.data.features;

public class StringFeature extends Feature {
	public StringFeature(String name) {
		this.setName(name);
		this.setDataType(Feature.DataType.STRING);
	}

	public String toString() {
		return "@attribute '" + this.name() + "' string";
	}
}

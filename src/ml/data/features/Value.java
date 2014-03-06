package ml.data.features;


public abstract class Value implements Comparable<Value> {
	private Feature _feature;
	
	public abstract boolean isMissing();
	
	public abstract Object value();
	
	public Feature feature() {
		return _feature;
	}
	
	protected void setFeature(Feature feature) {
		_feature = feature;
	}
	
	public Feature.DataType dataType() {
		return _feature.dataType();
	}

	@Override
	public int hashCode() {
		return this.value().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Value other = (Value) obj;
		if (this.feature().dataType()!=other.feature().dataType()) return false;
		return this.compareTo(other)==0;
	}
	
	public abstract int toInt();
	public abstract double toReal();
	
}
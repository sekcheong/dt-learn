package ml.data.features;

public class NumericValue extends Value {
	private double _value = Double.NaN;;
	
	public NumericValue(Feature attribute, double value) {
		this.setFeature(attribute);
		_value = value;
	}

	public double numericValue() {
		return _value;
	}
	
	@Override
	public int compareTo(Value o) {
		NumericValue v = (NumericValue) o;
		if (this.numericValue()==v.numericValue()) return 0;
		if (this.numericValue()<v.numericValue()) return -1;
		return 1;
	}

	@Override
	public boolean isMissing() {
		return _value==Double.NaN;
	}

	@Override
	public Object value() {
		return _value;
	}
	
	@Override
	public String toString() {
		//express non-fractional number in integer form
		if (Math.ceil(_value)==Math.floor(_value)) return Integer.toString((int) _value);
		return Double.toString(_value);
	}

	@Override
	public int toInt() {
		return (int) _value;
	}

	@Override
	public double toReal() {
		return _value;
	}

}

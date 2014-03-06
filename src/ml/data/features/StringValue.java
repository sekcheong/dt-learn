package ml.data.features;

public class StringValue extends Value {
	private String _value = null;
	
	public StringValue(Feature attribute, String value) {
		this.setFeature(attribute);
		_value = value;
	}
	
	public String stringValue() {
		return _value;
	}

	@Override
	public int compareTo(Value o) {
		StringValue v = (StringValue) o;
		return _value.compareTo(v.stringValue());
	}

	@Override
	public boolean isMissing() {
		return _value==null;
	}

	@Override
	public Object value() {
		return _value;
	}
	
	@Override
	public String toString() {
		return "'" + _value + "'";
	}

	@Override
	public int toInt() {
		return Integer.parseInt(_value);
	}

	@Override
	public double toReal() {
		return Double.parseDouble(_value);
	}

}

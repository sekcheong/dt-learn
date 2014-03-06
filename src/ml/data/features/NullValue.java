package ml.data.features;

public class NullValue extends Value {

	public NullValue(Feature attribute) {
		this.setFeature(attribute);
	}
	
	@Override
	public int compareTo(Value v) {
		if (v.isMissing()) return 0;
		return -1;
	}

	@Override
	public boolean isMissing() {
		return true;
	}

	@Override
	public Object value() {
		return null;
	}
	
	@Override 
	public String toString() {
		return "?";
	}

	@Override
	public int toInt() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double toReal() {
		// TODO Auto-generated method stub
		return 0;
	}

}

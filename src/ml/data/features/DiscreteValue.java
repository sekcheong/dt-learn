package ml.data.features;

public class DiscreteValue extends Value {
	private int _index = -1;
	
	public DiscreteValue(Feature attribute, String nominalName) throws Exception {
		this.setFeature(attribute);
		DiscreteFeature attr = (DiscreteFeature) this.feature();
		if (nominalName != null) {
			_index = attr.indexOf(nominalName);
			if (_index < 0) throw new Exception("Invalid nominal value name. Name:" + nominalName);
		}
	}
	
	public int discreteValue() {
		return _index;
	}
	
	public String discreteName() {
		return ((DiscreteFeature) this.feature()).nameAt(_index);
	}
	
	@Override
	public boolean isMissing() {
		return _index==-1;
	}

	@Override
	public Object value() {
		return _index;
	}

	@Override
	public int compareTo(Value arg)  {
		DiscreteValue v = (DiscreteValue) arg;
		if (v.discreteValue()==this.discreteValue()) return 0;
		if (v.discreteValue()<this.discreteValue()) return -1;
		return 1;
	}
	
	@Override
	public String toString() {
		return DiscreteFeature.formatName(this.discreteName());
	}

	@Override
	public int toInt() {
		return _index;
	}

	@Override
	public double toReal() {
		return (double) this.toInt();
	}

}

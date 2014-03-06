package ml.data;

import java.util.Iterator;

import ml.data.features.Feature;
import ml.data.features.Value;

public class Instance implements  Iterator<Value> {
	private Value[] _values;
	private int _pos = 0;
	
	public Instance(int length) {
		_values = new Value[length];
	}
	
	public void setValue(Value value) {
		_values[value.feature().index()] = value;
	}
	
	public Value values(Feature feature) {
		return _values[feature.index()];
	}
	
	public Value values(int index) {
		return _values[index];
	}
	
	public int length() {
		return _values.length;
	}

	@Override
	public boolean hasNext() {
		if (_pos < _values.length) return true;
		//rewind the position 
		_pos = 0;
		return false;
	}

	@Override
	public Value next() {
		return _values[_pos++];
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Value v : _values) {
			sb.append(v.toString());
			sb.append(",");
		}
		// remote the trailing ','
		if (sb.length() > 1) sb.setLength(sb.length() - 1);

		return sb.toString();
	}

}

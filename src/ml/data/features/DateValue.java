package ml.data.features;

import java.util.Date;

public class DateValue extends Value {
	private Date _value = null;
	
	public DateValue(Feature attribute, String dateString) throws Exception {
		this.setFeature(attribute);
		if (dateString!=null && !dateString.isEmpty()) {
			_value = ((DateFeature) attribute).dateFormat().parse(dateString);
		}
	}
	
	public Date dateValue() {
		return _value;
	}
	
	@Override
	public int compareTo(Value o) {
		return _value.compareTo(((DateValue) o).dateValue());
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
		return ((DateFeature) this.feature()).dateFormat().format(_value);
	}

	@Override
	public int toInt() {
		throw new UnsupportedOperationException();
	}

	@Override
	public double toReal() {
		return (double) _value.getTime();
	}

}

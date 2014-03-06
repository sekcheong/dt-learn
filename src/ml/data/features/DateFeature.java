package ml.data.features;

import java.text.SimpleDateFormat;

public class DateFeature extends Feature {
	private SimpleDateFormat _dateFormat;
	
	public DateFeature(String name) {
		initialize(name, null);
	}
	
	public DateFeature(String name, String format) {
		initialize(name, format);
	}

	private void initialize(String name, String format) {
		this.setName(name);
		this.setDataType(Feature.DataType.DATE);
		if (format!=null && !format.isEmpty()) {
			_dateFormat = new SimpleDateFormat(format);
		}
		else {
			_dateFormat = new SimpleDateFormat();
		}
	}
	
	public SimpleDateFormat dateFormat() {
		return _dateFormat;
	}
}

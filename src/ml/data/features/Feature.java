package ml.data.features;

public abstract class Feature {
	private String _name;
	private DataType _dataType;
	private int _index;
	
	public enum DataType {
		STRING,
		NUMERIC,
		DISCRETE,
		DATE
	}
	
	public String name() {
		return _name;
	}

	protected void setName(String name) {
		_name = name;
	}

	public DataType dataType() {
		return _dataType;
	}

	protected void setDataType(DataType dataType) {
		_dataType = dataType;
	}

	public int index() {
		return _index;
	}

	public void setIndex(int index) {
		_index = index;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_dataType == null) ? 0 : _dataType.hashCode());
		result = prime * result + _index;
		result = prime * result + ((_name == null) ? 0 : _name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		Feature other = (Feature) obj;
		if (_dataType != other._dataType) return false;
		if (_index != other._index) return false;
		if (_name == null) {
			if (other._name != null) return false;
		}
		else if (!_name.equals(other._name)) return false;
		return true;
	}
	
}

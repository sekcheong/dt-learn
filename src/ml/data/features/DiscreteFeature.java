package ml.data.features;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DiscreteFeature extends Feature{
	private Map<String, Integer> _namesMap;
	private ArrayList<String> _names;
	
	public DiscreteFeature(String name, ArrayList<String> nominalNames) {
		this.setName(name);
		this.setDataType(Feature.DataType.DISCRETE);
		_names = nominalNames;
	}
	
	public String nameAt(int index) {
		return _names.get(index);
	}
	
	public int indexOf(String name) {
		
		if (_namesMap == null) buildIndex();
		
		if (_namesMap.containsKey(name)) {
			return _namesMap.get(name);
		}
		
		return -1;
	}
	
	public int count() {
		return _names.size();
	}
	
	private void buildIndex() {
		_namesMap = new HashMap<String, Integer>();
		for (int i = 0; i < _names.size(); i++) {
			_namesMap.put(_names.get(i), i);
		}
	}
	
	public ArrayList<String> getNames() {
		return _names;
	}
	
	public boolean isValidName(String name) {
		return this.indexOf(name)!=-1;
	}
	
	public static String formatName(String name) {
		if (name.indexOf(' ')!=-1 || name.indexOf('%')!=-1) {
			return "'" + name + "'";
		}
		return name;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("@attribute '").append(this.name()).append("' ");
		sb.append("{");
		for (String s:_names) { 
			sb.append(formatName(s)).append(",");
		}
		if (sb.length()>1) sb.setLength(sb.length()-1);
		sb.append("}");
		return sb.toString();
	}
	
}

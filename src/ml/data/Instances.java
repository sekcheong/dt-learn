package ml.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Instances implements Iterable<Instance>{
	private List<Instance> _instances = new ArrayList<Instance>();
	
	public Instances(List<Instance> instances) {
		_instances = instances;
	}

	public Instances() {
		// TODO Auto-generated constructor stub
	}

	public Instance get(int index) {
		return _instances.get(index);
	}
	
	public void add(Instance newInstance) {
		_instances.add(newInstance);
	}
	
	public int count() {
		return _instances.size();
	}
	
	@Override
	public Iterator<Instance> iterator() {
		return _instances.iterator();
	}

}

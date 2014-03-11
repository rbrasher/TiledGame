package Util;

import java.util.HashMap;

public class RandomCollection<E> {
	
	private final HashMap<E, Double> map = new HashMap<E, Double>();
	private final HighQualityRandom random;
	private double total = 0;
	
	public RandomCollection() {
		this(new HighQualityRandom());
	}
	
	public RandomCollection(HighQualityRandom random) {
		this.random = random;
	}
	
	public void add(E result, double weight) {
		if(weight <= 0)
			return;
		
		total += weight;
		map.put(result, total);
	}
	
	public E next() {
		double value = random.nextDouble() * total;
		for(E item : map.keySet()) {
			Double weight = map.get(item);
			if(weight >= value)
				return item;
		}
		return null;
		// return map.keySet().iterator().next();
	}
	
}

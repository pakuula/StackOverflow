package traverse_ordered.simple;

import java.math.BigDecimal;
import java.util.Random;
import java.util.TreeSet;

import traverse_ordered.common.Cursor;
import traverse_ordered.common.OrderedArraySet;

public class RandomTest {
	public static final int numSets = 4;
	public static final int setSize = 50;
	
	public static OrderedArraySet<BigDecimal> mkSet() {
		TreeSet<BigDecimal> sorter = new TreeSet<BigDecimal>();
		Random prng = new Random();
		for (int i = 0; i < setSize; i++) {
			sorter.add(new BigDecimal(prng.nextDouble()*100));
		}
		return new OrderedArraySet<BigDecimal>(sorter);
	}
	public static void main(String[] args) {
		OrderedArraySet<BigDecimal>[] sets = new OrderedArraySet[numSets];
		for (int i = 0; i < numSets; i++) {
			sets[i] = mkSet();
		}
		Driver driver = new Driver(sets);
		int maxSize = 0;
		long cnt = 0;
		
		long start = System.currentTimeMillis();
		for (Cursor c : driver) {
			int size = driver.size();
			if (size > maxSize) {
				maxSize = size;
			}
			cnt ++;
		}
		long end = System.currentTimeMillis();
		
		System.out.println("Max queue size: " + maxSize
				+ ", number of tuples: " + cnt
				+ ", time per tuple (us): " + (end-start)/((double)cnt)*1000.0);
	}
}

package traverse_ordered.simple;

import java.util.Random;
import java.util.TreeSet;

import traverse_ordered.common.Cursor;

public class RandomTest {
	public static final int numSets = 5;
	public static final int setSize = 10;
	public static final int step = 1000000;
	
	public static double[] mkSet() {
		TreeSet<Double> sorter = new TreeSet<Double>();
		Random prng = new Random();
		for (int i = 0; i < setSize; i++) {
			sorter.add(prng.nextDouble()*100);
		}

		double[] result = new double[sorter.size()];
		int i = 0;
		for (Double d : sorter) {
			result[i] = d;
			i++;
		}
		return result;
	}
	
	public static void main(String[] args) {
		double[][] sets = new double[numSets][];
		for (int i = 0; i < numSets; i++) {
			sets[i] = mkSet();
		}
		Driver driver = new Driver(sets);
		long queueSize = 0;
		int maxSize = 0;
		long cnt = 0;
		
		long start = System.currentTimeMillis();
		for (@SuppressWarnings("unused") Cursor c : driver) {
			int size = driver.size();
			if (size > maxSize) {
				maxSize = size;
			}
			queueSize += size;
			cnt ++;
			if (cnt%step == 0) {
				long end = System.currentTimeMillis();
				double avgSize = queueSize/(double)step;
				System.out.println("Chunk " + cnt/step
						+ ", max queue size: " + maxSize
						+ ", average queue size: " + avgSize
						+ ", number of tuples: " + cnt
						+ ", time per tuple (us): " + (end-start)/((double)cnt)*1000.0);
				queueSize = 0;
			}
		}
		long end = System.currentTimeMillis();
		
		System.out.println("Max queue size: " + maxSize
				+ ", number of tuples: " + cnt
				+ ", time per tuple (us): " + (end-start)/((double)cnt)*1000.0);
	}
}

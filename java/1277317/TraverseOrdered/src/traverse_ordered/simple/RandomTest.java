package traverse_ordered.simple;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import traverse_ordered.common.Cursor;

public class RandomTest {
	public static final int numSets = 3;
	public static final int setSize = 3;
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
	
	static String arrayToString(double[] arr) {
		List<String> strs = new ArrayList<String>();
		for (double d: arr) {
			strs.add(Double.toString(d));
		}
		return String.join("\t", strs);
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		double[][] sets = new double[numSets][];
		for (int i = 0; i < numSets; i++) {
			sets[i] = mkSet();
		}
		Driver driver = new Driver(sets);
		long queueSize = 0;
		int maxSize = 0;
		long maxmem = 0;
		long cnt = 0;
		long m0 = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
		
		System.out.println("Initial memory: " + m0);
		
		String fname = ""+System.currentTimeMillis()/1000;
		PrintWriter setFile = new PrintWriter(fname+".set.txt");
		for (double[] set : sets) {
			setFile.println(arrayToString(set));
		}
		setFile.close();
		
		File ofile = new File(fname +".data.txt");
		System.out.println(ofile.getAbsolutePath());
		PrintWriter out = new PrintWriter(ofile);
		
		long start = System.currentTimeMillis();
		for (Cursor c : driver) {
			System.out.println(c+":"+c.sum());
			
			int size = driver.size();
			out.println(Double.toString(c.sum())+"\t"+size + "\t#" + c);
			if (size > maxSize) {
				maxSize = size;
				maxmem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
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
						+ ", time per tuple (us): " + (end-start)*1000.0/cnt
						+ ", memory footprint " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1e6
				);
				
				queueSize = 0;
			}
		}
		long end = System.currentTimeMillis();
		out.close();
		
		System.out.println("Max queue size: " + maxSize + "(" + maxSize*100.0/cnt + ")%"
				+ ", number of tuples: " + cnt
				+ ", time per tuple (us): " + (end-start)*1000.0/cnt
				+ ", max mem (mb): " + maxmem/1e6
				);
	}
}

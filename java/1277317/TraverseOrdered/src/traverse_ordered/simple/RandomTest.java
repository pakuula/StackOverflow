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
	public static final int numSets = 10;
	public static final int setSize = 5;
	public static final int step = 1000000;
	
	public static final boolean saveData = false;
	
	private String fname;
	private PrintWriter setsFile;
	private PrintWriter dataFile;
	
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
		new RandomTest().run(numSets, setSize, saveData);
	}

	public void run(int numSets, int setSize, boolean saveData) throws FileNotFoundException {
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
		
		if (saveData) {
			fname = ""+System.currentTimeMillis()/1000;
			setsFile = new PrintWriter(fname+".set.txt");
			for (double[] set : sets) {
				setsFile.println(arrayToString(set));
			}
			setsFile.close();
			File ofile = new File(fname +".data.txt");
			System.out.println(ofile.getAbsolutePath());
			dataFile = new PrintWriter(ofile);
		}
		
		long start = System.currentTimeMillis();
		for (Cursor c : driver) {
			int size = driver.size();
			if (saveData) {
				dataFile.println(Double.toString(c.sum())+"\t"+size);
			}
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
		if (saveData) {
			dataFile.close();
		}
		
		System.out.println("Max queue size: " + maxSize + "(" + maxSize*100.0/cnt + ")%"
				+ ", number of tuples: " + cnt
				+ ", time per tuple (us): " + (end-start)*1000.0/cnt
				+ ", max mem (mb): " + maxmem/1e6
				);
		
		double share = maxSize/(double)cnt;
		double predicted = 2/(setSize*Math.sqrt(numSets)*Math.sqrt(Math.PI/6));
		System.out.println("Max tuples: " + share*100 + "%" 
				+ ", predicted: " + predicted*100 + "%"
				);

	}
	
}

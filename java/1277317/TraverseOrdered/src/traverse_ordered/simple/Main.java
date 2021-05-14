package traverse_ordered.simple;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import traverse_ordered.common.Cursor;
import traverse_ordered.common.OrderedArraySet;

public class Main {
	public static double[] mkSet(double... elems) {
		return elems;
	}
	public static double[] mkSet(int... elems) {
		double[] result = new double[elems.length];
		for (int i = 0; i < elems.length; i++) {
			result[i] = elems[i];
		}
		return result;
	}

	@SafeVarargs
	public static void run(double[]... sets) {
		Driver driver = new Driver(sets);
		int i = 1;
		for (Cursor c : driver) {
			System.out.println(i++ + ": sum: " + c.sum() + ", row:" + Arrays.toString(c.get()));
		}
	}
	
	public static void main(String[] args) {
		{
			System.out.println("Test 1:");
			double[] os1 = mkSet(1,2);
			double[] os2 = mkSet(10,100);
			double[] os3 = mkSet(10,20);
			
			run(os1, os2, os3);
		}
		{
			System.out.println("Test 2:");
			double[] os1 = mkSet(0,1,2);
			double[] os2 = mkSet(0,1,2);
			double[] os3 = mkSet(0,1,2);
			
			run(os1, os2, os3);
		} 
	}
}

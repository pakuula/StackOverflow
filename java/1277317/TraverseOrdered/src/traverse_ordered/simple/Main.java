package traverse_ordered.simple;

import java.math.BigDecimal;
import java.util.ArrayList;

import traverse_ordered.common.Cursor;
import traverse_ordered.common.OrderedArraySet;

public class Main {
	public static OrderedArraySet<BigDecimal> mkSet(double... elems) {
		ArrayList<BigDecimal> result = new ArrayList<>();
		for (double d: elems) {
			result.add(new BigDecimal(d));
		}
		return new OrderedArraySet<BigDecimal>(result);
	}
	public static OrderedArraySet<BigDecimal> mkSet(int... elems) {
		ArrayList<BigDecimal> result = new ArrayList<>();
		for (int d: elems) {
			result.add(new BigDecimal(d));
		}
		return new OrderedArraySet<BigDecimal>(result);
	}

	@SafeVarargs
	public static void run(OrderedArraySet<BigDecimal>... sets) {
		Driver driver = new Driver(sets);
		int i = 1;
		for (Cursor c : driver) {
			System.out.println(i++ + ": sum: " + c.sum() + ", row:" + c.get().toString());
		}
	}
	public static void main(String[] args) {
		{
			System.out.println("Test 1:");
			OrderedArraySet<BigDecimal> os1 = mkSet(1,2);
			OrderedArraySet<BigDecimal> os2 = mkSet(10,100);
			OrderedArraySet<BigDecimal> os3 = mkSet(10,20);
			
			run(os1, os2, os3);
		}
		{
			System.out.println("Test 2:");
			OrderedArraySet<BigDecimal> os1 = mkSet(0,1,2);
			OrderedArraySet<BigDecimal> os2 = mkSet(0,1,2);
			OrderedArraySet<BigDecimal> os3 = mkSet(0,1,2);
			
			run(os1, os2, os3);
		} 
	}
}

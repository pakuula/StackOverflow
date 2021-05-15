package traverse_ordered.common;

import java.util.Arrays;

/**
 * Набор индексов для набора упорядоченных множеств
 */
public class Cursor implements Comparable<Cursor> {
	public static final double epsilon = 1e-10;
	public static boolean almostEquals(double d1, double d2) {
		return Math.abs(d1 - d2) < epsilon;
	}
	public static int almostCompare(double d1, double d2) {
		boolean eq = almostEquals(d1,d2);
		if (eq) return 0;
		
		return Double.compare(d1, d2);
	}
	/**
	 * набор упорядоченных множеств
	 */
	private double[][] arrays;
	/**
	 * набор индексов
	 */
	private int[] index;
	private double _cached_sum = Double.NaN;
	
	public Cursor(double[][] arrays) {
		this.arrays = arrays;
		this.index = new int[arrays.length];
		Arrays.fill(index, 0);
		sum();
	}
	
	protected Cursor(double[][] arrays, int[] index, double sum) {
		this.arrays = arrays;
		this.index = index;
		_cached_sum = sum;
	}
	
	/**
	 * Возвращает кортеж значений, соответствующих индексу.
	 * @return
	 */
	public double[] get() {
		double[] result = new double[arrays.length];
		for (int i = 0; i < index.length; i++) {
			result[i] = arrays[i][index[i]];
		}
		return result;
	}

	/**
	 * Возвращает сумму значений элементов кортежа.
	 * @return
	 */
	public double sum() {
		if (Double.isNaN(_cached_sum)) {
			double result = 0.0;
			for (int i = 0; i < index.length; i++) {
				result += arrays[i][index[i]];
			}
			_cached_sum = result;
		}
		return _cached_sum;
	}

	/**
	 * Проверяет, что в множестве с номером n достигнут предел.
	 * @param n номер множества в наборе.
	 * @return true если индекс для множества n достиг максимума.
	 */
	public boolean isDone(int n) {
		return index[n] >= arrays[n].length - 1;
	}

	/**
	 * Строит набор индексов, в котором индекс для множества с номером n, увеличен на 1.
	 * Для всех остальных множеств индекс остаётся тем же.
	 * @param n индекс множества
	 * @return набор индексов
	 */
	public Cursor inc(int n) {
		if (isDone(n)) {
			throw new IllegalArgumentException("The set " + n + " is exhausted");
		}
		
		int[] new_index = index.clone();
		new_index[n] += 1;
		double new_sum = _cached_sum - arrays[n][index[n]] + arrays[n][new_index[n]];
		Cursor result = new Cursor(arrays, new_index, new_sum);
		
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(arrays);
		result = prime * result + Arrays.hashCode(index);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cursor other = (Cursor) obj;
		if (!Arrays.equals(index, other.index))
			return false;
		return true;
	}

	/**
	 * Индексы упорядочены лексикографически.
	 */
	@Override
	public int compareTo(Cursor o) {
		int sumCmp = almostCompare(sum(), o.sum());
		if (sumCmp != 0) {
			return sumCmp;
		} else {
			return Arrays.compare(index, o.index);
		}
	}

	@Override
	public String toString() {
		return "Cursor[" + Arrays.toString(index) + "]";
	}
}

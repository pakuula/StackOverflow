package traverse_ordered.common;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Набор индексов для набора упорядоченных множеств
 */
public class Cursor implements Comparable<Cursor> {
	/**
	 * набор упорядоченных множеств
	 */
	private OrderedArraySet<BigDecimal>[] arrays;
	/**
	 * набор индексов
	 */
	private int[] index;
	
	public Cursor(OrderedArraySet<BigDecimal>[] arrays) {
		this.arrays = arrays;
		this.index = new int[arrays.length];
		Arrays.fill(index, 0);
	}
	
	protected Cursor(OrderedArraySet<BigDecimal>[] arrays, int[] index) {
		this.arrays = arrays;
		this.index = index;
	}
	
	/**
	 * Возвращает кортеж значений, соответствующих индексу.
	 * @return
	 */
	public ArrayList<BigDecimal> get() {
		ArrayList<BigDecimal> result = new ArrayList<BigDecimal>();
		for (int i = 0; i < index.length; i++) {
			result.add(arrays[i].get(index[i]));
		}
		return result;
	}

	/**
	 * Возвращает сумму значений элементов кортежа.
	 * @return
	 */
	public BigDecimal sum() {
		BigDecimal result = BigDecimal.ZERO;
		for (int i = 0; i < index.length; i++) {
			result = result.add(arrays[i].get(index[i]));
		}
		return result;
	}

	/**
	 * Проверяет, что в множестве с номером n достигнут предел.
	 * @param n номер множества в наборе.
	 * @return true если индекс для множества n достиг максимума.
	 */
	public boolean isDone(int n) {
		return index[n] >= arrays[n].size() - 1;
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
		
		Cursor result = new Cursor(arrays, index.clone());
		result.index[n] += 1;
		
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
		return Arrays.compare(index, o.index);
	}

	@Override
	public String toString() {
		return "Cursor[" + Arrays.toString(index) + "]";
	}
}

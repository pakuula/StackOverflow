package traverse_ordered.simple;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

import traverse_ordered.common.Cursor;
import traverse_ordered.common.OrderedArraySet;
import traverse_ordered.common.SortedMultiSet;

/**
 * Перебирает индексы набора упорядоченных множеств таким образом, 
 * что генерируются кортежи в порядке возрастания суммы элементов.
 */
public class Driver implements Iterable<Cursor>, Iterator<Cursor> {
	/**
	 * Набор упорядоченных множеств
	 */
	private double[][] arrays;
	/**
	 * Фронт следующих вершин графа, упорядоченных по возрастанию суммы
	 */
	// private SortedMultiSet<Edge> edges;
	private TreeSet<Cursor> queue;
	
	@SafeVarargs
	public Driver(double[]... arrays) {
		this.arrays = arrays;
		queue = new TreeSet<Cursor>();
		queue.add(new Cursor(arrays));
	}
	
	public int size() {
		return queue.size();
	}

	/**
	 * Итератор наборов индексов
	 */
	@Override
	public Iterator<Cursor> iterator() {
		return this;
	}

	@Override
	public boolean hasNext() {
		return !queue.isEmpty();
	}

	/**
	 * Построение следующего набора индексов
	 */
	@Override
	public Cursor next() {
		Cursor result = queue.pollFirst();
		// Для извлечённого набора индексов добавляем в фронт всех детей
		addKids(result);
		return result;
	}
	
	/**
	 * Добавление всех дуг, исходящих из заданного набора индексов
	 * @param c набор индексов
	 */
	private void addKids(Cursor c) {
		for (int i = 0; i < arrays.length; i++) {
			if (!c.isDone(i)) {
				Cursor kid = c.inc(i);
				queue.add(kid);
			}
		}
	}
}

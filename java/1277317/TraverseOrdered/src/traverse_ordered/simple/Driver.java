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
	private OrderedArraySet<BigDecimal>[] arrays;
	/**
	 * Фронт следующих вершин графа, упорядоченных по возрастанию суммы
	 */
	private SortedMultiSet<Edge> edges;
	private boolean started = false;
	
	@SafeVarargs
	public Driver(OrderedArraySet<BigDecimal>... arrays) {
		this.arrays = arrays;
		edges = new SortedMultiSet<>();
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
		return !started || !edges.isEmpty();
	}

	/**
	 * Построение следующего набора индексов
	 */
	@Override
	public Cursor next() {
		Cursor result = null;
		if (!started) {
			// Самый первый набор индексов - все индексы равны 0
			result = new Cursor(arrays);
			started = true;
		} else {
			// Берём из фронта перебора набор индексов с минимальной суммой
			result = edges.pollFirst().end();
		}
		// Для извлечённого набора индексов добавляем в фронт всех детей
		addEdges(result);
		return result;
	}
	
	/**
	 * Добавление всех дуг, исходящих из заданного набора индексов
	 * @param c набор индексов
	 */
	private void addEdges(Cursor c) {
		for (int i = 0; i < arrays.length; i++) {
			if (!c.isDone(i)) {
				Edge e = new Edge(c, i);
				edges.add(e);
			}
		}
	}
}

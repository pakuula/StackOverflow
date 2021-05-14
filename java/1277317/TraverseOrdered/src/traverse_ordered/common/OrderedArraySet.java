package traverse_ordered.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Упорядоченное множество элементов с доступом к произвольному элементу по номеру.
 */
public class OrderedArraySet<T> {
	private ArrayList<T> elems;
	
	public OrderedArraySet(Collection<? extends T> elems) {
		SortedSet<T> sorter = new TreeSet<T>(elems);
		this.elems = new ArrayList<T>();
		this.elems.addAll(sorter);
	}
	
	public int size() {
		return this.elems.size();
	}
	
	public T get(int index) {
		return this.elems.get(index);
	}
}

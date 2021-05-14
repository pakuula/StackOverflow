package traverse_ordered.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

// Список элементов, отсортированных в естественном порядке.
// для каждого элемента elem нет других элементов elem1 таких, чтобы elem.equals(elem1)
public class SortedMultiSet<T extends Comparable<? super T>> {
	private ArrayList<T> lst = new ArrayList<>();
	
	public SortedMultiSet() {
	}
	
	public boolean add(T elem) {
		if (lst.isEmpty()) {
			lst.add(elem);
			return true;
		}
		int pos = Collections.binarySearch(lst, elem);
		if (pos >= 0) {
			// Если есть элементы, равные в смысле compareTo, нужно убедиться, 
			// что нет элементов равных в смысле equals
			int found = -1;
			// сначала поищем в сторону начала списка
			for (int i = pos; i >= 0; i--) {
				T item = lst.get(i);
				if (elem.compareTo(item) != 0) {
					break;
				}
				if (elem.equals(item)) {
					found = i;
					break;
				}
			}
			if (found < 0) {
				// потом поищем в стороу конца списка
				for (int i = pos+1; i < lst.size(); i++) {
					T item = lst.get(i);
					if (elem.compareTo(item) != 0) {
						break;
					}
					if (elem.equals(item)) {
						found = i;
						break;
					}
				}
			}
			if (found >= 0) {
				// если нашли, то элемент не добавляем
				return false;
			}
		} else {
			// binsearch возвращает рекомендуемую позицию для добавления как -(pos+1)
			pos = (-pos) - 1;
		}
		lst.add(pos, elem);
		return true;
	}
	
	public boolean isEmpty() {
		return lst.isEmpty();
	}
	
	// Возвращает первый элемент и удаляет его
	public T pollFirst() {
		T result = lst.get(0);
		lst.remove(0);
		return result;
	}
}

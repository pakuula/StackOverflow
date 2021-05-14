package traverse_ordered.simple;

import java.math.BigDecimal;

import traverse_ordered.common.Cursor;

/**
 * Дуга в графе перебора кортежей набора множеств.
 */
public class Edge implements Comparable<Edge>{
	/**
	 * Вершина, в которую ведёт дуга
	 */
	private Cursor end;
	/**
	 * Вес дуги - сумма значений кортежа, соответствующего конечной вершине дуги.
	 */
	private BigDecimal _sum_cached;
	
	public Edge(Cursor start, int n) {
		end = start.inc(n);
		_sum_cached = end.sum();
	}
	
	public BigDecimal sum() {
		return _sum_cached;
	}

	public Cursor end() {
		return end;
	}
	
	/**
	 * Естественный порядок: по весу дуги.
	 */
	@Override
	public int compareTo(Edge o) {
		int sumCompare =  sum().compareTo(o.sum());
		return sumCompare;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((end == null) ? 0 : end.hashCode());
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
		Edge other = (Edge) obj;
		if (!_sum_cached.equals(other._sum_cached)) {
			return false;
		}
		if (end == null) {
			if (other.end != null)
				return false;
		} else if (!end.equals(other.end))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Edge [end=" + end + ", _sum_cached=" + _sum_cached + "]";
	}

}

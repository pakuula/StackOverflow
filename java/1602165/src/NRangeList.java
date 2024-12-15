import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Класс NRangeList представляет собой список диапазонов одинаковых чисел,
 * идущих подряд в массиве.
 * 
 * Он позволяет добавлять новые диапазоны, проверять, пуст ли список, обрезать
 * список до максимального количества уникальных значений,
 * а также получать начальную и конечную позиции диапазонов и их суммарную
 * длину.
 *
 * Пример использования:
 * 
 * <pre>
 * NRangeList nRangeList = new NRangeList(3);
 * nRangeList.add(1, 5); // число 1 повторяется 5 раз
 * nRangeList.add(2, 10); // число 2 повторяется 5 раз
 * nRangeList.add(3, 15); // число 3 повторяется 5 раз
 * nRangeList.add(4, 20); // Список будет обрезан до 3 уникальных значений
 * </pre>
 */
public class NRangeList {

    /**
     * Список объектов диапазонов значений.
     */
    private List<NRange> nRanges;
    /**
     * Множество уникальных значений N.
     */
    private Set<Integer> cache = new HashSet<Integer>();
    
    /**
     * Максимальное количество уникальных элементов.
     * 
     * Если при добавлении очередного диапазона количество уникальных значений превысит это значение,
     * список диапазонов будет обрезан до максимального количества уникальных значений.
     */
    public final int MAX_UNIQUE_COUNT;

    /**
     * Конструктор для создания списка диапазонов.
     *
     * @param uniqueCount максимальное количество уникальных значений N
     */
    public NRangeList(int uniqueCount) {
        if (uniqueCount < 1) {
            throw new IllegalArgumentException("Unique count should be greater than 0");
        }
        MAX_UNIQUE_COUNT = uniqueCount;
        nRanges = new java.util.ArrayList<>();
    }

    /**
     * Проверяет, пуст ли список диапазонов.
     *
     * @return true, если список пуст, иначе false
     */
    public boolean isEmpty() {
        return nRanges.isEmpty();
    }

    /**
     * Обрезает список диапазонов до максимального количества уникальных значений N.
     *
     * @return true, если список был обрезан, иначе false
     */
    public boolean truncate() {
        if (cache.size() <= MAX_UNIQUE_COUNT) {
            return false;
        }
        var set = new HashSet<Integer>();
        for (int i = nRanges.size() - 1; i >= 0; i--) {
            var r = nRanges.get(i);
            if (set.contains(r.N)) {
                continue;
            }
            if (set.size() < MAX_UNIQUE_COUNT) {
                set.add(r.N);
                continue;
            }
            nRanges.subList(0, i + 1).clear();
            cache = set;
            return true;
        }
        throw new AssertionError("Should not reach here");
    }

    /**
     * Добавляет новый диапазон в список. Диапазон начинается с последнего диапазона
     * в списке.
     * 
     * Если при добавлении нового диапазона дисло уникальных значений N превышает
     * максимальное количество,
     * список диапазонов обрезается до максимального количества уникальных значений
     * N.
     *
     * @param N   число, которое повторяется в диапазоне
     * @param end конечная позиция диапазона
     * @return    true, если список был обрезан после добавления, иначе false
     */
    public boolean add(Integer N, int end) {
        if (end <= this.end()) {
            throw new IllegalArgumentException("Range end is not greater than the last range end");
        }
        cache.add(N);
        nRanges.add(new NRange(N, end(), end));
        return truncate();
    }

    /**
     * Возвращает начальную позицию первого диапазона в списке.
     *
     * @return начальная позиция первого диапазона, или 0, если список пуст
     */
    public int start() {
        if (isEmpty()) {
            return 0;
        }
        return nRanges.getFirst().range.start;
    }

    /**
     * Возвращает конечную позицию последнего диапазона в списке.
     *
     * @return конечная позиция последнего диапазона, или 0, если список пуст
     */
    public int end() {
        if (isEmpty()) {
            return 0;
        }
        return nRanges.getLast().range.end;
    }

    /**
     * Возвращает суммарную длину диапазонов из списка.
     *
     * @return длина диапазона
     */
    public int len() {
        return end() - start();
    }

    /**
     * Внутренний класс NRange представляет диапазон одинаковых чисел, идущих подряд
     * в массиве.
     */
    private static class NRange {
        /**
         * Число, которое повторяется в диапазоне.
         */
        public final Integer N;
        /**
         * Диапазон индексов массива.
         */
        public final Range range;

        /**
         * Конструктор для создания диапазона NRange.
         *
         * @param n     уникальный идентификатор диапазона
         * @param start начальная позиция диапазона
         * @param end   конечная позиция диапазона
         */
        public NRange(Integer n, int start, int end) {
            N = n;
            range = new Range(start, end);
        }

        /**
         * Возвращает строковое представление объекта NRange.
         *
         * @return строковое представление объекта NRange
         */
        @Override
        public String toString() {
            return "NRange{" + "N=" + N + ", range=" + range + '}';
        }
    }
}

/**
 * Класс Solver2 предоставляет методы для поиска подмассивов с ограниченным
 * числом уникальных элементов.</br>
 * 
 * Для поиска подмассива максимальной длины алгоритм делает один проход.
 * Хранится список диапазонов одинаковых значений.</br>
 * 
 * Константы:
 * <ul>
 * <li>DEFAULT_UNIQUE_COUNT - значение по умолчанию для uniqueCount.
 * </ul>
 * Методы:
 * <ul>
 * <li>bestRange(): Находит и возвращает лучший диапазон в массиве, где число
 * уникальных элементов не превышает uniqueCount.
 * </ul>
 * 
 * @param arr         массив целых чисел, в котором выполняется поиск.
 * @param uniqueCount максимальное количество уникальных элементов в подмассиве.
 * 
 */
public class Solver2 {
    /**
     * Массив целых чисел, в котором выполняется поиск.
     */
    private final Integer[] arr;
    /**
     * Максимальное количество уникальных элементов в подмассиве.
     */
    private final int uniqueCount;
    /**
     * Значение по умолчанию для uniqueCount.
     */
    public static final int DEFAULT_UNIQUE_COUNT = 3;

    /**
     * Конструктор для создания объекта Solver2.
     *
     * @param arr         массив целых чисел, в котором выполняется поиск.
     * @param uniqueCount максимальное количество уникальных элементов в подмассиве.
     */
    public Solver2(Integer[] arr, int uniqueCount) {
        if (uniqueCount < 1) {
            throw new IllegalArgumentException("Unique count should be greater than 0");
        }
        this.arr = arr;
        this.uniqueCount = uniqueCount;
    }

    /**
     * Конструктор для создания объекта Solver2 с uniqueCount по умолчанию.
     *
     * @param arr массив целых чисел, в котором выполняется поиск.
     */
    public Solver2(Integer[] arr) {
        this(arr, DEFAULT_UNIQUE_COUNT);
    }

    /**
     * Находит и возвращает лучший диапазон в массиве.</br>
     * Лучший диапазон определяется как самый длинный подмассив,
     * в котором число уникальных элементов не превышает uniqueCount.
     *
     * @return объект Range, представляющий лучший диапазон в массиве.
     */
    public Range bestRange() {
        if (arr.length == 0) {
            return new Range(0, 0);
        }

        NRangeList ranges = new NRangeList(uniqueCount);
        Range maxRange = new Range(0, 0);

        for (int i = skipSameVal(0); i < arr.length; i = skipSameVal(i)) {
            var prev = arr[i-1];

            ranges.add(prev, i);
            if (ranges.len() > maxRange.len()) {
                maxRange = new Range(ranges.start(), ranges.end());
            }
        }
        ranges.add(arr[arr.length-1], arr.length);
        if (ranges.len() > maxRange.len()) {
            maxRange = new Range(ranges.start(), ranges.end());
        }
        return maxRange;
    }

    /**
     * Пропускает элементы массива с одинаковыми значениями, начиная с указанного
     * индекса.
     * 
     * @param start начальный индекс для проверки.
     * @return индекс первого элемента, который отличается от элемента на позиции
     *         start,
     *         или длину массива, если все последующие элементы одинаковы.
     */
    private int skipSameVal(int start) {
        var v = arr[start];
        for (int i = start + 1; i < arr.length; i++) {
            if (arr[i] != v) {
                return i;
            }
        }
        return arr.length;
    }
}
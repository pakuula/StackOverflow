
import java.util.HashSet;

/**
 * Класс Solver1 предоставляет методы для поиска подмассивов с ограниченным
 * числом уникальных элементов.</br>
 * 
 * Используется алгоритм "грубой силы", который пробегает по всем значениям
 * индексов и предпросматривает подмассивы вперёд, как если бы лучший подмассив
 * начинался с текущего индекса.</br>
 * 
 * Константы:
 * <ul>
 * <li>DEFAULT_UNIQUE_COUNT - значение по умолчанию для uniqueCount.
 * </ul>
 * Методы:
 * <ul>
 * <li>matchingEnd(int start): Возвращает индекс конца подмассива, в котором
 * содержится не более uniqueCount уникальных элементов.
 * <li>bestRange(): Находит и возвращает лучший диапазон в массиве, где число
 * уникальных элементов не превышает uniqueCount.
 * </ul>
 * 
 * @param arr         массив целых чисел, в котором выполняется поиск.
 * @param uniqueCount максимальное количество уникальных элементов в подмассиве.
 * 
 */
public class Solver1 {
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
     * Конструктор для создания объекта Solver1.
     *
     * @param arr         массив целых чисел, в котором выполняется поиск.
     * @param uniqueCount максимальное количество уникальных элементов в подмассиве.
     */
    public Solver1(Integer[] arr, int uniqueCount) {
        if (uniqueCount < 1) {
            throw new IllegalArgumentException("Unique count should be greater than 0");
        }
        this.arr = arr;
        this.uniqueCount = uniqueCount;
    }

    /**
     * Конструктор для создания объекта Solver1 с uniqueCount по умолчанию.
     *
     * @param arr массив целых чисел, в котором выполняется поиск.
     */
    public Solver1(Integer[] arr) {
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
        Range best = new Range(0, 0);
        for (int i = 0; i < arr.length - best.len(); i = skipSameVal(i)) {
            var end = matchingEnd(i);
            if (end - i > best.len()) {
                best = new Range(i, end);
            }
        }
        return best;
    }

    
    /**
     * Возвращает индекс конца подмассива, в котором содержится не более uniqueCount
     * уникальных элементов.
     * Подмассив начинается с индекса start.
     *
     * @param start начальный индекс для поиска.
     * @return индекс первого элемента после конца искомого подмассива.
     * @throws IndexOutOfBoundsException если start находится вне массива.
     */
    public int matchingEnd(int start) {
        if (start < 0 || start >= arr.length) {
            throw new IndexOutOfBoundsException();
        }
        var numbers = new HashSet<Integer>(); // Множество уникальных элементов подмассива.
        numbers.add(start);
        for (var i = skipSameVal(start) ; i < arr.length; skipSameVal(start)) {
            var v = arr[i];
            if (numbers.contains(v)) {
                // Этот элемент уже есть в подмассиве
                continue;
            }
            if (numbers.size() == uniqueCount) {
                // Подмассив должен содержать не более uniqueCount уникальных элементов.
                // Добавление текущего элемента приведёт к превышению лимита.
                return i;
            }
            // Добавляем текущий элемент в множество уникальных элементов.
            numbers.add(v);
        }
        return arr.length;
    }

    /**
     * Пропускает элементы массива с одинаковыми значениями, начиная с указанного индекса.
     * 
     * @param start начальный индекс для проверки.
     * @return индекс первого элемента, который отличается от элемента на позиции start,
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

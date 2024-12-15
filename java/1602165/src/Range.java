/**
 * Класс представляет диапазон индексов в массиве.
 */
public class Range {
    /**
     * Начальная позиция диапазона.
     */
    public final int start;
    /**
     * Конечная позиция диапазона.
     */
    public final int end;

    /**
     * Конструктор.
     *
     * @param start начальная позиция диапазона
     * @param end   конечная позиция диапазона
     */
    public Range(int start, int end) {
        if (start < 0 || end < 0 || start > end) {
            throw new IllegalArgumentException("Invalid range: " + start + ":" + end);
        }
        this.start = start;
        this.end = end;
    }
    
    /**
     * Возвращает длину диапазона.
     *
     * @return длина диапазона
     */
    public int len() {
        return end - start;
    }

    @Override
    public String toString() {
        return "Range [start=" + start + ", end=" + end + "]";
    }

}
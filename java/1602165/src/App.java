import java.util.Arrays;

public class App {
    public static void main(String[] args) throws Exception {
        Integer[] arr = { 4, 5, 3, 3, 7, 3, 3, 7, 6, 4, 6, 7, 7, 7, 7, 1 };

        var s1 = new Solver1(arr, 3);
        for (int i = 0; i < arr.length; i++) {
            var end = s1.matchingEnd(i);
            var subArr = Arrays.asList(arr).subList(i, end);
            System.out.println("" + i + ":" + (end - i) + ":" + subArr);
        }
        var range1 = s1.bestRange();
        System.err.println(
                "max subarray : " + range1 + ":" + Arrays.asList(arr).subList(range1.start, range1.end));

        var s2 = new Solver2(arr);
        var range2 = s2.bestRange();
        System.err.println("max subarray2: " + range2 + ":"
                + Arrays.asList(arr).subList(range2.start, range2.end));
    }
}

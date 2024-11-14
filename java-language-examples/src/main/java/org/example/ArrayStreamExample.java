package org.example;

import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.stream.IntStream;

public class ArrayStreamExample {
    public static void main(String[] args) {
        findMaxMin();

        checkPallindromeString();

        sumAndAvg();

        rangeGenerator();

        mergeIntStreams();
    }

    private static void mergeIntStreams() {
        int[] even = {2, 4, 6};
        int[] odd = {3, 5, 7};

        int[] sorted = IntStream.concat(Arrays.stream(even), Arrays.stream(odd)).sorted().toArray();
        System.out.println(Arrays.toString(sorted));
    }

    private static void rangeGenerator() {
        IntStream.range(0, 5).forEach(System.out::println);
        IntStream.rangeClosed(0, 5).forEach(System.out::println);
    }

    private static void sumAndAvg() {
        int[] arr = {1, 2, 3, 4};
        int max = Arrays.stream(arr).max().getAsInt();
        int min = Arrays.stream(arr).min().getAsInt();
        System.out.println(max);
        System.out.println(min);

        int sum = Arrays.stream(arr).sum();
        double avg = Arrays.stream(arr).average().getAsDouble();

        System.out.println(sum);
        System.out.println(avg);
    }

    private static void findMaxMin() {
        int[] arr = {1, 2, 3, 4};
        IntSummaryStatistics stats = Arrays.stream(arr).summaryStatistics();
        System.out.println(stats.getMax());
        System.out.println(stats.getMin());
    }

    private static void checkPallindromeString() {
        String s = "MADAM";
        boolean result = IntStream.range(0, s.length()).allMatch(i ->
                s.charAt(i) == s.charAt(s.length() - 1 - i));
        System.out.println(result);
    }
}

package org.example;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GroupAnagrams {

    public static void main(String[] args) {
        String arr[] = {"abcd", "java", "dcba", "ajav", "xyz", "epam", "pame", "aepm"};
        Map<String, List<String>> resultMap = Arrays.stream(arr).
                collect(Collectors.groupingBy(GroupAnagrams::createKey, LinkedHashMap::new, Collectors.toList()));
        System.out.println(resultMap);
        List<List<String>> groups =
                resultMap.values().stream().filter(e -> e.size() > 1).collect(Collectors.toList());
        System.out.println(groups); // o(k) + o(nlognk) = o(k)
    }

    public static String createKey(String element) {
        int[] occurrences = new int[26];
        Arrays.fill(occurrences, -1);
        for (char c : element.toCharArray()) {
            int v = c - 97;
            occurrences[v] = occurrences[v] + 1;
        }
        StringBuilder keyBuilder = new StringBuilder();
        for (int index = 0; index < occurrences.length; index++) {
            int v = occurrences[index];
            if (v == -1) {
                continue;
            }
            keyBuilder.append("#");
            keyBuilder.append((char) (index + 97));
            keyBuilder.append("_" + v);
        }
        return keyBuilder.toString();
    }
}

package net.pladform.cribbage.engine;

import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Dan Barrese
 */
public class Sum15Test {

    @SuppressWarnings("unchecked")
    @Test
    public void test() throws Exception {
        Map<String, Integer> handSum15s = new HashMap<>();
        Set<String> sum15s = new HashSet<>();
        for (int a = 1; a <= 10; a++) {
            for (int b = 1; b <= 10; b++) {
                for (int c = 1; c <= 10; c++) {
                    for (int d = 1; d <= 10; d++) {
                        for (int e = 1; e <= 10; e++) {
                            if (a + b + c + d + e == 15) {
                                if (Stream.of(a, b, c, d, e).collect(Collectors.toSet()).size() > 1) {
                                    handle(sum15s, a, b, c, d, e);
                                }
                            }
                        }
                        if (a + b + c + d == 15) {
                            handle(sum15s, a, b, c, d);
                        }
                    }
                    if (a + b + c == 15) {
                        handle(sum15s, a, b, c);
                    }
                }
                if (a + b == 15) {
                    handle(sum15s, a, b);
                }
            }
        }
        System.out.println();
        System.out.println(sum15s);
        System.out.println(sum15s.size());

        for (int a = 1; a <= 10; a++) {
            for (int b = 1; b <= 10; b++) {
                for (int c = 1; c <= 10; c++) {
                    for (int d = 1; d <= 10; d++) {
                        for (int e = 1; e <= 10; e++) {
                            String hand = Stream.of(a, b, c, d, e)
                                    .sorted()
                                    .map(i -> i == 10 ? "T" : String.valueOf(i))
                                    .collect(Collectors.joining());
                            if (handSum15s.containsKey(hand)) {
                                continue;
                            }
                            if (Stream.of(a, b, c, d, e).distinct().count() > 1) {
                                int[] all5 = new int[]{a, b, c, d, e};
                                combinations(all5, 5, 2)
                                        .forEach(gram2 -> {
                                                if (sum15s.contains(gram2)) {
                                                    handSum15s.compute(hand, (s, times15) -> times15 == null ? 1 : times15 + 1);
                                                }
                                        });
                                combinations(all5, 5, 3)
                                        .forEach(gram3 -> {
                                            if (sum15s.contains(gram3)) {
                                                handSum15s.compute(hand, (s, times15) -> times15 == null ? 1 : times15 + 1);
                                            }
                                        });
                                combinations(all5, 5, 4)
                                        .forEach(gram4 -> {
                                            if (sum15s.contains(gram4)) {
                                                handSum15s.compute(hand, (s, times15) -> times15 == null ? 1 : times15 + 1);
                                            }
                                        });
                                combinations(all5, 5, 5)
                                        .forEach(gram5 -> {
                                            if (sum15s.contains(gram5)) {
                                                handSum15s.compute(hand, (s, times15) -> times15 == null ? 1 : times15 + 1);
                                            }
                                        });
                            }
                        }
                    }
                }
            }
        }
        System.out.println();
        System.out.println(String.format("!!: %s", handSum15s));
        System.out.println(handSum15s.size());
    }

    public void handle(Set<String> sum15s, Integer... nums) {
        String s = Stream.of(nums)
                .sorted()
                .map(i -> i == 10 ? "T" : String.valueOf(i))
                .collect(Collectors.joining());
        sum15s.add(s);
    }

    // following code from: http://www.geeksforgeeks.org/print-all-possible-combinations-of-r-elements-in-a-given-array-of-size-n/

    /* arr[]  ---> Input Array
    data[] ---> Temporary array to store current combination
    start & end ---> Staring and Ending indexes in arr[]
    index  ---> Current index in data[]
    r ---> Size of a combination to be printed */
    static void combinationUtil(
            int arr[],
            int data[],
            int start,
            int end,
            int index,
            int r,
            List<String> accumulator
    ) {
        // Current combination is ready to be printed, print it
        if (index == r) {
            List<Integer> combo = new ArrayList<>(r);
            for (int j = 0; j < r; j++) {
                combo.add(data[j]);
            }
            accumulator.add(combo.stream()
                    .sorted()
                    .map(i -> i == 10 ? "T" : String.valueOf(i))
                    .collect(Collectors.joining())
            );
            return;
        }

        // replace index with all possible elements. The condition
        // "end-i+1 >= r-index" makes sure that including one element
        // at index will make a combination with remaining elements
        // at remaining positions
        for (int i = start; i <= end && end - i + 1 >= r - index; i++) {
            data[index] = arr[i];
            combinationUtil(arr, data, i + 1, end, index + 1, r, accumulator);
        }
    }

    // The main function that prints all combinations of size r
    // in arr[] of size n. This function mainly uses combinationUtil()
    static List<String> combinations(int arr[],
                                    int n,
                                    int r) {
        // A temporary array to store all combination one by one
        int data[] = new int[r];

        // Print all combination using temprary array 'data[]'
        List<String> accumulator = new ArrayList<>();
        combinationUtil(arr, data, 0, n - 1, 0, r, accumulator);
        return accumulator;
    }

    static void permute(List<Integer> arr, int k, Set<String> accumulator) {
        for (int i = k; i < arr.size(); i++) {
            Collections.swap(arr, i, k);
            permute(arr, k + 1, accumulator);
            Collections.swap(arr, k, i);
        }
        if (k == arr.size() - 1) {
            String s = arr.stream()
                    .sorted()
                    .map(i -> i == 10 ? "T" : String.valueOf(i))
                    .collect(Collectors.joining());
            accumulator.add(s);
        }
    }

    static Set<String> permutations(Integer[] nums) {
        Set<String> accumulator = new HashSet<>();
        permute(Arrays.asList(nums), 0, accumulator);
        return accumulator;
    }

    @Test
    public void ljkdsjfklj() throws Exception {
        int[] all5 = new int[]{10, 5, 5, 1, 1};
        combinations(all5, 5, 2)
                .forEach(gram2 -> {
                    System.out.println(gram2);
                });
    }

}
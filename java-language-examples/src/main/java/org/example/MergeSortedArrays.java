package org.example;

public class MergeSortedArrays {
    public static void main(String[] args) {
        int[] arr1 = {10, 20, 30, 40, 50};
        int[] arr2 = {5, 10, 15, 20, 25, 30, 35, 40, 50};

        int i = 0;
        int j = 0;

        while (true) {

            if (i > arr1.length - 1 || j > arr2.length - 1) {
                break;
            }

            if (arr1[i] < arr2[j]) {
                System.out.println(arr1[i]);
                i++;
            } else if (arr1[i] > arr2[j]) {
                System.out.println(arr2[j]);
                j++;
            } else {
                System.out.println(arr1[i]);
                i++;
                j++;
            }

        }

        while (i < arr1.length - 1) {
            System.out.println(arr1[i]);
            i++;
        }

        while (j < arr2.length - 1) {
            System.out.println(arr2[j]);
            j++;
        }

    }
}

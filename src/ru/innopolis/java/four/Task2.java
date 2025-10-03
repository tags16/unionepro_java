package ru.innopolis.java.four;

import java.util.Arrays;
import java.util.Scanner;

public class Task2 {
    public static boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        char[] arr1 = s.toLowerCase().toCharArray();
        char[] arr2 = t.toLowerCase().toCharArray();

        Arrays.sort(arr1);
        Arrays.sort(arr2);

        return Arrays.equals(arr1, arr2);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Введите первую строку:");
        String s = sc.nextLine();
        System.out.println("Введите вторую строку:");
        String t = sc.nextLine();

        System.out.println(isAnagram(s, t));
        sc.close();
    }
}

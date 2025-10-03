package ru.innopolis.java.four;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PowerfulSet {

    // Пересечение
    public static <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>(set1);
        result.retainAll(set2);
        return result;
    }

    // Объединение
    public static <T> Set<T> union(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>(set1);
        result.addAll(set2);
        return result;
    }

    // Разность множеств
    public static <T> Set<T> relativeComplement(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>(set1);
        result.removeAll(set2);
        return result;
    }

    public static void main(String[] args) {
        Set<Integer> set1 = new HashSet<>(Arrays.asList(1, 2, 3));
        Set<Integer> set2 = new HashSet<>(Arrays.asList(0, 1, 2, 4));

        System.out.println("Пересечение: " + intersection(set1, set2));
        System.out.println("Объединение: " + union(set1, set2));
        System.out.println("Разность: " + relativeComplement(set1, set2));
    }
}
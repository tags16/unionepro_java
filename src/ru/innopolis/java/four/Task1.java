package ru.innopolis.java.four;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Task1 {
    public static <T> Set<T> getUnique(ArrayList<T> list) {
        return new HashSet<>(list);
    }

    public static void main(String[] args) {
        ArrayList<String> names = new ArrayList<>();
        names.add("Иван");
        names.add("Иван");
        names.add("Петр");
        names.add("Иван");
        names.add("Анна");
        names.add("Борис");

        Set<String> uniqueNames = getUnique(names);
        System.out.println(uniqueNames);
    }
}

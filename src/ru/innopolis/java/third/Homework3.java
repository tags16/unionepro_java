package ru.innopolis.java.third;

import java.util.Arrays;
import java.util.Scanner;

public class Homework3 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Задача 1
        System.out.println("Задача 1: Введите букву английского алфавита:");
        char inputChar = scanner.next().charAt(0);
        String keyboard = "qwertyuiopasdfghjklzxcvbnm";
        int index = keyboard.indexOf(inputChar);
        char leftChar;
        if (index == -1) {
            System.out.println("Ошибка: это не маленькая буква английского алфавита!");
        } else {
            // Выводим букву слева с учётом замкнутости
            leftChar = keyboard.charAt((index - 1 + keyboard.length()) % keyboard.length());
            System.out.println("Слева от '" + inputChar + "' находится '" + leftChar + "'");
        }

        // Задача 2
        System.out.println("\nЗадача 2: Введите строку из символов '>', '<', '-' :");
        String arrowsStr = scanner.next();
        int n = arrowsStr.length();
        int count = 0;
        for (int i = 0; i + 5 <= n; i++) {
            String sub = arrowsStr.substring(i, i + 5);
            if (sub.equals(">>-->") || sub.equals("<--<<")) {
                count++;
            }
        }

        System.out.println("Кол-во стрел: " + count);

        // Задача 3
        System.out.println("\nЗадача 3: Введите два слова через пробел:");
        scanner.nextLine();
        String input = scanner.nextLine().toLowerCase();
        String[] words = input.split(" ");
        for (String word: words){
            char[] letters = word.toCharArray();
            Arrays.sort(letters);
            System.out.print(new String(letters) + " ");
            
        }
        scanner.close();
    }
}

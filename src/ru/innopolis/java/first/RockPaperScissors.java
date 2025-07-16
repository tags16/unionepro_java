package ru.innopolis.java.first;

import java.util.Random;

public class RockPaperScissors {
    
    public static void main(String[] args){
        Random random = new Random();

        int vasyaInt = random.nextInt(3);
        int petyaInt = random.nextInt(3);

        System.out.println("Вася выбрал: " + convertNumberToText(vasyaInt));
        System.out.println("Петя выбрал: " + convertNumberToText(petyaInt));
        if (vasyaInt == petyaInt){
            System.out.print("ничья");
        } else if ((vasyaInt == 0 && petyaInt == 1) || 
                   (vasyaInt == 1 && petyaInt == 2) || 
                   (vasyaInt == 2 && petyaInt == 0)) {
            System.out.println("Вася выиграл!");
        } else{
            System.out.print("Петя выйграл");
        }
    }

    public static String convertNumberToText(int number) {
        return switch (number) {
            case 0 -> "Камень";
            case 1 -> "Ножницы";
            case 2 -> "Бумага";
            default -> "default";
        };
    }
}

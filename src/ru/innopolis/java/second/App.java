package ru.innopolis.java.second;

import java.util.Random;
import java.util.Scanner;

class Television {
    private String brand;
    private int size;
    private boolean isOn;
    private int channel;

    // Конструктор
    public Television(String brand, int size, boolean isOn, int channel) {
        this.brand = brand;
        this.size = size;
        this.isOn = isOn;
        this.channel = channel;
    }

    // Конструктор без параметров
    public Television() {
        this.brand = "Samsung";
        this.size = 40;
        this.isOn = false;
        this.channel = 1;
    }

    public String getBrand() {
        return brand;
    }

    public int getSize() {
        return size;
    }

    public boolean isOn() {
        return isOn;
    }

    public int getChannel() {
        return channel;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setIsOn(boolean isOn) {
        this.isOn = isOn;
    }
    
    public void turnOn() {
        this.isOn = true;
        System.out.println("Телевизов включен");
    }

    public void turnOff() {
        this.isOn = false;
        System.out.println("Телевизор выключен");
    }

    public void setChannel(int channel) {
        if (isOn) {
            this.channel = channel;
            System.out.println("Установлен канал: " + channel);
        } else {
            System.out.println("Телевизор выключен. Невозможно установить канал.");
        }
    }

    public void showInfo() {
        System.out.println("Бренд: " + brand + 
        ", Диалональ: " + size +
        ", Состояние: " + isOn +
        ", Канал: " + channel);
    }
}

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        // 1. Создаем телевизор через конструктов с параметрами
        Television tv1 = new Television("LG", 55, true, 1);

        // 2. Создаем телевизор с клавиатуры
        System.out.print("Введите бренд телевизора: ");
        String brand = scanner.nextLine();
        System.out.print("Введите диагональ телевизора: ");
        int size = scanner.nextInt();
        System.out.print("Введите состояние телевизора (true/false): ");
        boolean isOn = scanner.nextBoolean();
        System.out.print("Введите канал телевизора: ");
        int channel = scanner.nextInt();
        Television tv2 = new Television(brand, size, isOn, channel);

        // 3. Создаем телевизор с рандомом
        String[] brands = {"Samsung", "LG", "Sony", "Philips", "Panasonic"};
        Television tv3 = new Television(
            brands[random.nextInt(brands.length)],
            30 + random.nextInt(40),
            random.nextBoolean(),
            1 + random.nextInt(50)
        );

        tv1.showInfo();
        tv2.showInfo();
        tv3.showInfo();

        tv1.setChannel(16);
        tv1.turnOff();
        tv1.showInfo();

        scanner.close();
    }
}
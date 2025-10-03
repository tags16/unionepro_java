package ru.innopolis.java.five;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // Создадим много машинок)
        List<Car> cars = Arrays.asList(
            new Car("a123me", "Mercedes", "White", 0, 8300000),
            new Car("b873of", "Volga", "Black", 0, 673000),
            new Car("w487mn", "Lexus", "Grey", 76000, 900000),
            new Car("p987hj", "Volga", "Red", 610, 704340),
            new Car("c987ss", "Toyota", "White", 254000, 761000),
            new Car("o983op", "Toyota", "Black", 698000, 740000),
            new Car("p146op", "BMW", "White", 271000, 850000),
            new Car("u893ii", "Toyota", "Purple", 210900, 440000),
            new Car("l097df", "Toyota", "Black", 108000, 780000),
            new Car("y876wd", "Toyota", "Black", 160000, 1000000)
        );

        // Отобразим что создали
        System.out.println("Автомобили в базе:");
        System.out.println("Number Model Color Mileage Cost");
        cars.forEach(System.out::println);

        // Зададим  переменные для выборки
        String colorToFind = "Black";
        long mileageToFind = 150_000L;
        long minPrice = 500_000L;
        long maxPrice = 900_000L;
        String modelToFind = "Toyota";
        String modelNotExist = "Volvo";

        // 1) номера по цвету или пробегу
        String numbers = cars.stream()
                .filter(c -> c.getColor().equalsIgnoreCase(colorToFind) || c.getMileage() == mileageToFind)
                .map(Car::getNumber)
                .collect(Collectors.joining(" "));
        System.out.println("Номера автомобилей по цвету или пробегу: " + numbers);

        // 2) количество уникальных моделей в диапазоне цен
        long uniqueModels = cars.stream()
                .filter(c -> c.getCost() >= minPrice && c.getCost() <= maxPrice)
                .map(Car::getModel)
                .distinct()
                .count();
        System.out.println("Уникальные автомобили: " + uniqueModels + " шт.");

        // 3) цвет авто с минимальной стоимостью
        String minCostColor = cars.stream()
                .min(Comparator.comparingLong(Car::getCost))
                .map(Car::getColor)
                .orElse("Не найдено");
        System.out.println("Цвет автомобиля с минимальной стоимостью: " + minCostColor);

        // 4) средняя стоимость модели
        double avgToyota = cars.stream()
                .filter(c -> c.getModel().equalsIgnoreCase(modelToFind))
                .mapToLong(Car::getCost)
                .average()
                .orElse(0);
        double avgVolvo = cars.stream()
                .filter(c -> c.getModel().equalsIgnoreCase(modelNotExist))
                .mapToLong(Car::getCost)
                .average()
                .orElse(0);

        System.out.printf("Средняя стоимость модели %s: %.2f%n", modelToFind, avgToyota);
        System.out.printf("Средняя стоимость модели %s: %.2f%n", modelNotExist, avgVolvo);
    }
}

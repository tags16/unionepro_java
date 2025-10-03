package ru.innopolis.java.five;

import java.util.Objects;

public class Car {
    private String number;
    private String model;
    private String color;
    private long mileage;
    private long cost;

    public Car(String number, String model, String color, long mileage, long cost) {
        this.number = number;
        this.model = model;
        this.color = color;
        this.mileage = mileage;
        this.cost = cost;
    }

    // Определяем геттеры и сеттеры для полей нашего класса
    // Номер автомобиля
    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number; }
    
    // Модель
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    // Цвет
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    // Пробег
    public long getMileage() { return mileage; }
    public void setMileage(long mileage) { this.mileage = mileage; }

    // Стоимость
    public long getCost() { return cost; }
    public void setCost(long cost) { this.cost = cost; }

    @Override
    public String toString() {
        return number + " " + model + " " + color + " " + mileage + " " + cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Car)) return false;
        Car car = (Car) o;
        return Objects.equals(number, car.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}

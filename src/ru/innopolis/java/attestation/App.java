package ru.innopolis.java.attestation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

class Product {
    private final String name;
    private final double price;

    public Product(String name, double price) {
        validateName(name);
        validatePrice(price);
        this.name = name;
        this.price = price;
    }

    protected void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Название продукта не может быть пустым");
        }
        if (name.length() < 3) {
            throw new IllegalArgumentException("Название должно быть не короче 3 символов");
        }
        if (name.matches("\\d+")) {
            throw new IllegalArgumentException("Название не должно состоять только из цифр");
        }
    }

    protected void validatePrice(double price) {
        if (price <= 0) {
            throw new IllegalArgumentException("Цена должна быть положительной");
        }
    }

    public String getName() { return name; }

    public double getPrice() { return price; }

    @Override
    public String toString() {
        return name + " (" + price + " руб.)";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product product = (Product) o;
        return Double.compare(product.price, price) == 0 && Objects.equals(name, product.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, price);
    }
}

class DiscountProduct extends Product {
    private final double discount;       // размер скидки в %
    private final LocalDate expiryDate;  // срок действия скидки

    public DiscountProduct(String name, double price, double discount, LocalDate expiryDate) {
        super(name, price);
        if (discount < 0 || discount >= 100) {
            throw new IllegalArgumentException("Скидка должна быть от 0 до 99%");
        }
        if (expiryDate == null) {
            throw new IllegalArgumentException("Дата окончания скидки обязательна");
        }
        this.discount = discount;
        this.expiryDate = expiryDate;
    }

    @Override
    public double getPrice() {
        if (LocalDate.now().isBefore(expiryDate.plusDays(1))) { // пока дата не вышла
            double discountedPrice = super.getPrice() * (1 - discount / 100);
            if (discountedPrice <= 0) {
                throw new IllegalArgumentException("Цена со скидкой должна быть положительной");
            }
            return discountedPrice;
        }
        return super.getPrice();
    }

    @Override
    public String toString() {
        return super.getName() + " (скидка " + discount + "%, до " + expiryDate + ")";
    }
}

class Person {
    private String name;
    private double money;
    private List<Product> bag;

    public Person(String name, double money) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (name.length() < 3) {
            throw new IllegalArgumentException("Имя не может быть короче 3 символов");
        }
        if (money < 0) {
            throw new IllegalArgumentException("Деньги не могут быть отрицательными");
        }
        this.name = name;
        this.money = money;
        this.bag = new ArrayList<>();
    }

    public String getName() { return name; }

    public void buyProduct(Product product) {
        if (money >= product.getPrice()) {
            bag.add(product);
            money -= product.getPrice();
            System.out.println(name + " купил " + product.getName());
        } else {
            System.out.println(name + " не может позволить себе " + product.getName());
        }
    }

    public void printlnPurchases() {
        if (bag.isEmpty()) {
            System.out.println(name + " - Ничего не куплено");
        } else {
            String products = String.join(", ", bag.stream().map(Product::getName).toList());
            System.out.println(name + " - " + products);
        }
    }

    @Override
    public String toString() {
        return name + " (" + money + " руб.)";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return Double.compare(person.money, money) == 0 && Objects.equals(name, person.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, money);
    }
}


public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        
        Map<String, Person> people = new LinkedHashMap<>();
        Map<String, Product> products = new LinkedHashMap<>();

        // Ввод покупателей
        System.out.println("Введите покупателей (Имя = деньги), пустая строка для завершения:");
        while (true) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) break;
            try {
                String[] parts = line.split("=");
                String name = parts[0].trim();
                double money = Double.parseDouble(parts[1].trim());
                people.put(name, new Person(name, money));
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        // Ввод продуктов
        System.out.println("Введите продукты (Название = цена) или (Название = цена : скидка% : ГГГГ-ММ-ДД), пустая строка для завершения:");
        while (true) {
            String line = sc.nextLine().trim();
            if (line.isEmpty()) break;
            try {
                if (line.contains(":")) {
                    // Формат: Название = цена : скидка% : 2025-08-20
                    String[] parts = line.split("=");
                    String name = parts[0].trim();
                    String[] values = parts[1].split(":");
                    double price = Double.parseDouble(values[0].trim());
                    double discount = Double.parseDouble(values[1].replace("%", "").trim());
                    LocalDate expiry = LocalDate.parse(values[2].trim());
                    products.put(name, new DiscountProduct(name, price, discount, expiry));
                } else {
                    String[] parts = line.split("=");
                    String name = parts[0].trim();
                    double price = Double.parseDouble(parts[1].trim());
                    products.put(name, new Product(name, price));
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        // Процесс покупок
        System.out.println("Введите покупки (Имя - Название), END для завершения:");
        while (true) {
            String line = sc.nextLine().trim();
            if (line.equalsIgnoreCase("END")) break;
            String[] parts = line.split("-");
            if (parts.length == 2) {
                String buyerName = parts[0].trim();
                String productName = parts[1].trim();

                Person buyer = people.get(buyerName);
                Product product = products.get(productName);
                if (buyer != null && product != null) {
                    buyer.buyProduct(product);
                }
            }
        }

        // Итоги
        for (Person p : people.values()) {
            p.printlnPurchases();
        }
        sc.close();
    }
}

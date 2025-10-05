package com.example.orders;

import com.example.orders.dao.CustomerDao;
import com.example.orders.dao.OrderDao;
import com.example.orders.dao.ProductDao;
import com.example.orders.model.Customer;
import com.example.orders.model.OrderRecord;
import com.example.orders.model.Product;
import com.example.orders.util.DbUtil;
import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Properties;

/**
 * Приложение демонстрирует:
 * - миграции (Flyway)
 * - CRUD операций: вставка product/customer, создание заказа, чтение последних 5 заказов, обновление, удаление
 * - транзакцию с commit/rollback
 */
public class App {
    public static void main(String[] args) {
        System.out.println("--- Orders JDBC Demo ---");

        try {
            // 1. Загрузка конфигурации и миграции
            Properties cfg = DbUtil.loadProperties();
            runMigrations(cfg);

            // 2. Инициализация DAO
            ProductDao productDao = new ProductDao();
            CustomerDao customerDao = new CustomerDao();
            OrderDao orderDao = new OrderDao();

            // 3. Демонстрация CRUD операций
            performCrudOperations(productDao, customerDao, orderDao);

        } catch (Exception e) {
            System.err.println("Application failed with an error: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\n--- All done. Bye. ---");
    }

    private static void runMigrations(Properties cfg) {
        boolean useFlyway = Boolean.parseBoolean(cfg.getProperty("flyway.enabled", "true"));
        if (useFlyway) {
            System.out.println("Running Flyway migrations...");
            Flyway flyway = Flyway.configure()
                    .dataSource(cfg.getProperty("db.url"), cfg.getProperty("db.user"), cfg.getProperty("db.password"))
                    .load();
            flyway.migrate();
            System.out.println("Migrations finished.");
        }
    }

    private static void performCrudOperations(ProductDao productDao, CustomerDao customerDao, OrderDao orderDao) throws SQLException {
        // --- CREATE ---
        System.out.println("\n[CREATE] Inserting new product and customer...");
        Product newProduct = new Product(0, "Тестовый продукт", 999.99, 10, null);
        int newProductId = productDao.insert(newProduct);
        System.out.println("-> Inserted product with id=" + newProductId);

        Customer newCustomer = new Customer(0, "Тест", "Тестов", "+79990001122", "test@example.com");
        int newCustomerId = customerDao.insert(newCustomer);
        System.out.println("-> Inserted customer with id=" + newCustomerId);

        // --- TRANSACTION ---
        System.out.println("\n[TRANSACTION] Creating an order for the new customer...");
        try (Connection conn = DbUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Создание заказа и обновление количества товара атомарно
                int orderId = orderDao.createOrder(conn, newProductId, newCustomerId, 2, "NEW");
                productDao.decreaseQuantity(conn, newProductId, 2);
                conn.commit();
                System.out.println("-> Order created (id=" + orderId + ") and product quantity updated. Transaction committed.");
            } catch (SQLException ex) {
                conn.rollback();
                System.err.println("-> Transaction rolled back due to: " + ex.getMessage());
            }
        }

        // --- READ ---
        System.out.println("\n[READ] Last 5 orders:");
        List<OrderRecord> lastOrders = orderDao.findLastOrders(5);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (OrderRecord r : lastOrders) {
            System.out.printf(" - id=%d, date=%s, customer=%s %s, product=%s, qty=%d, status=%s, total=%.2f%n",
                    r.id(), r.orderDate().format(fmt), r.firstName(), r.lastName(), r.productDescription(),
                    r.qty(), r.statusName(), r.price() * r.qty());
        }

        // --- UPDATE ---
        System.out.println("\n[UPDATE] Updating price and quantity for product id=" + newProductId);
        productDao.updatePrice(newProductId, 799.00);
        productDao.updateQuantity(newProductId, 20);
        System.out.println("-> Product updated.");

        // --- DELETE ---
        System.out.println("\n[DELETE] Cleaning up test records...");
        orderDao.deleteOrdersByCustomer(newCustomerId);
        customerDao.deleteById(newCustomerId);
        productDao.deleteById(newProductId);
        System.out.println("-> Cleanup done.");
        
        // --- READ (additional) ---
        System.out.println("\n[READ] Top 3 products by sold quantity:");
        productDao.printTopProducts(3);
    }
}
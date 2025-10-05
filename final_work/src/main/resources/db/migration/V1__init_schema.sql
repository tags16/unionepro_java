-- V1__init_schema.sql
-- Схема для учёта заказов

-- таблица категорий
CREATE TABLE IF NOT EXISTS category (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

COMMENT ON TABLE category IS 'Категории продуктов';

-- товары
CREATE TABLE IF NOT EXISTS product (
    id SERIAL PRIMARY KEY,
    "description" TEXT NOT NULL,
    price NUMERIC(12,2) NOT NULL CHECK (price >= 0),
    quantity INT NOT NULL CHECK (quantity >= 0),
    category_id INT,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(id)
);

COMMENT ON TABLE product IS 'Товары';
COMMENT ON COLUMN product."description" IS 'Описание товара';
COMMENT ON COLUMN product.price IS 'Цена товара';
COMMENT ON COLUMN product.quantity IS 'Количество на складе';

CREATE INDEX IF NOT EXISTS idx_product_category ON product(category_id);

-- клиенты
CREATE TABLE IF NOT EXISTS customer (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(50),
    email VARCHAR(200)
);

COMMENT ON TABLE customer IS 'Клиенты';

-- справочник статусов заказов
CREATE TABLE IF NOT EXISTS order_status (
    id SERIAL PRIMARY KEY,
    "name" VARCHAR(50) NOT NULL
);

COMMENT ON TABLE order_status IS 'Статусы заказов';

-- заказы
CREATE TABLE IF NOT EXISTS orders (
    id SERIAL PRIMARY KEY,
    product_id INT NOT NULL,
    customer_id INT NOT NULL,
    order_date TIMESTAMP NOT NULL DEFAULT now(),
    qty INT NOT NULL CHECK (qty > 0),
    status_id INT NOT NULL,
    CONSTRAINT fk_orders_product FOREIGN KEY (product_id) REFERENCES product(id),
    CONSTRAINT fk_orders_customer FOREIGN KEY (customer_id) REFERENCES customer(id),
    CONSTRAINT fk_orders_status FOREIGN KEY (status_id) REFERENCES order_status(id)
);

COMMENT ON TABLE orders IS 'Заказы';

CREATE INDEX IF NOT EXISTS idx_orders_product ON orders(product_id);
CREATE INDEX IF NOT EXISTS idx_orders_customer ON orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_orders_date ON orders(order_date);

-- Тестовые данные: категории
INSERT INTO category ("name") SELECT x FROM (VALUES
  ('Еда'),('Напитки'),('Электроника'),('Товары для дома'),('Книги')
) AS t(x)
ON CONFLICT DO NOTHING;

-- Тестовые продукты
INSERT INTO product ("description", price, quantity, category_id) SELECT * FROM (VALUES
  ('Хлеб', 40.00, 100, 1),
  ('Молоко', 60.00, 200, 2),
  ('Торт', 1000.00, 10, 1),
  ('Кофе растворимый', 879.00, 50, 2),
  ('Масло', 150.00, 70, 1),
  ('Чай', 200.00, 80, 2),
  ('Смартфон', 45000.00, 5, 3),
  ('Пылесос', 12000.00, 7, 4),
  ('Книга Java', 1500.00, 20, 5),
  ('Шоколад', 120.00, 60, 1)
) AS t("desc",pr,q,cat)
ON CONFLICT DO NOTHING;

-- Тестовые клиенты
INSERT INTO customer (first_name, last_name, phone, email) SELECT * FROM (VALUES
  ('Павел','Андреевич','+70000000001','pavel@example.com'),
  ('Анна','Петровна','+70000000002','anna@example.com'),
  ('Борис','Иванов','+70000000003','boris@example.com'),
  ('Женя','Сидоров','+70000000004','jenya@example.com'),
  ('Света','Алексеева','+70000000005','sveta@example.com'),
  ('Иван','Петров','+70000000006','ivan@example.com'),
  ('Мария','Кузнецова','+70000000007','maria@example.com'),
  ('Дмитрий','Смирнов','+70000000008','dmitriy@example.com'),
  ('Ольга','Козлова','+70000000009','olga@example.com'),
  ('Никита','Горбунов','+70000000010','nikita@example.com')
) AS t(fn,ln,ph,em)
ON CONFLICT DO NOTHING;

-- Статусы заказов
INSERT INTO order_status ("name") SELECT x FROM (VALUES
  ('NEW'), ('PROCESSING'), ('SHIPPED'), ('CANCELLED'), ('COMPLETED')
) AS t(x)
ON CONFLICT DO NOTHING;

-- Тестовые заказы — распределим по клиентам и продуктам
INSERT INTO orders (product_id, customer_id, order_date, qty, status_id) SELECT * FROM (VALUES
  (1, 1, now() - interval '2 days', 2, 1),
  (2, 2, now() - interval '1 day', 1, 3),
  (3, 3, now() - interval '10 days', 1, 5),
  (4, 2, now() - interval '3 days', 1, 2),
  (5, 1, now() - interval '7 days', 3, 5),
  (6, 4, now() - interval '4 days', 2, 1),
  (7, 5, now() - interval '20 days', 1, 4),
  (8, 6, now() - interval '15 days', 1, 3),
  (9, 7, now() - interval '6 days', 2, 1),
  (10, 8, now() - interval '8 days', 5, 2)
) AS t(pid,cid,dt,q,s)
ON CONFLICT DO NOTHING;

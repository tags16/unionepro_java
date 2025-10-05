-- test-queries.sql

-- 1. Список всех заказов за последние 7 дней с именем покупателя и описанием товара.
SELECT 
    o.id, 
    o.order_date, 
    c.first_name || ' ' || c.last_name AS customer,
    p.description, o.qty, 
    s.name AS status
FROM orders o
JOIN customer c ON o.customer_id = c.id
JOIN product p ON o.product_id = p.id
JOIN order_status s ON o.status_id = s.id
WHERE 
    o.order_date >= now() - interval '7 days'
ORDER BY o.order_date DESC;

-- 2. Топ-3 самых популярных товара (по суммарному количеству заказов)
SELECT 
    p.id, 
    p.description, 
    SUM(o.qty) AS total_sold
FROM orders o
JOIN product p ON o.product_id = p.id
GROUP BY p.id, p.description
ORDER BY total_sold DESC
LIMIT 3;

-- 3. Количество клиентов без заказов
SELECT 
    COUNT(*) 
FROM customer c
LEFT JOIN orders o ON c.id = o.customer_id
WHERE 
    o.id IS NULL;

-- 4. Сумма выручки по товарам (агрегация)
SELECT 
    p.id, 
    p.description, 
    SUM(o.qty * p.price) AS revenue
FROM orders o
JOIN product p ON o.product_id = p.id
GROUP BY p.id, p.description
ORDER BY revenue DESC;

-- 5. Список заказов определённого клиента (пример client id = 2)
SELECT 
    o.id, 
    o.order_date, 
    p.description, 
    o.qty, 
    s.name
FROM orders o
JOIN product p ON o.product_id = p.id
JOIN order_status s ON o.status_id = s.id
WHERE o.customer_id = 2
ORDER BY o.order_date DESC;

-- --- UPDATE запросы
-- 6. UPDATE: уменьшение количества на складе при покупке (пример уменьшаем товар id=1 на 2)
UPDATE product SET quantity = quantity - 2 WHERE id = 1;

-- 7. UPDATE: изменение цены товара
UPDATE product SET price = price * 0.95 WHERE id = 4;

-- 8. UPDATE: смена статуса заказа
UPDATE orders SET status_id = (SELECT id FROM order_status WHERE name = 'PROCESSING' LIMIT 1) WHERE id = 1;

-- --- DELETE запросы
-- 9. Удаление старых заказов старше 365 дней
DELETE FROM orders WHERE order_date < now() - interval '365 days';

-- 10. Удаление клиентов без заказов
DELETE FROM customer c WHERE NOT EXISTS (SELECT 1 FROM orders o WHERE o.customer_id = c.id);

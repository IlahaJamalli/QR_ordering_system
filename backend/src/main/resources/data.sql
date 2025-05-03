-- -----------------------
-- Preload table_entity
-- -----------------------

INSERT INTO restaurant_table (id, table_number, qr_code_url)
VALUES 
(1, 'A1', 'qr_codes/table_A1.png')
ON CONFLICT DO NOTHING;

-- -----------------------
-- Preload menu_item
-- -----------------------

INSERT INTO menu_item (id, name, description, price, category)
VALUES 
(1, 'Cheeseburger', 'Juicy beef patty with cheese', 8.99, 'Main Course'),
(2, 'French Fries', 'Crispy golden fries', 3.49, 'Appetizers'),
(3, 'Cola', 'Refreshing cold beverage', 1.99, 'Drinks'),
(4, 'Caesar Salad', 'Fresh romaine lettuce with Caesar dressing', 6.99, 'Appetizers'),
(5, 'Chocolate Cake', 'Rich chocolate layered cake', 4.99, 'Desserts'),
(6, 'Grilled Chicken', 'Herb-marinated grilled chicken breast', 10.49, 'Main Course'),
(7, 'Spaghetti Bolognese', 'Classic Italian pasta with meat sauce', 9.99, 'Main Course'),
(8, 'Garlic Bread', 'Toasted bread with garlic and butter', 2.99, 'Appetizers'),
(9, 'Orange Juice', 'Freshly squeezed orange juice', 2.49, 'Drinks'),
(10, 'Mineral Water', 'Sparkling mineral water', 1.50, 'Drinks'),
(11, 'Ice Cream', 'Vanilla or chocolate ice cream', 3.50, 'Desserts'),
(12, 'Fruit Salad', 'Assorted seasonal fruits', 3.99, 'Desserts')
ON CONFLICT DO NOTHING;

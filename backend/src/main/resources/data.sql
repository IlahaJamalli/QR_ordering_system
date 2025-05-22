
-- -----------------------
-- Delete old data
-- -----------------------
DELETE FROM menu_item;
DELETE FROM restaurant_table;

-- -----------------------
-- Preload restaurant_table
-- -----------------------
INSERT INTO restaurant_table (id, table_number, qr_code_url)
VALUES
    (1, 'A1', 'qr_codes/table_A1.png');

-- -----------------------
-- Preload menu_item
-- -----------------------
INSERT INTO menu_item (id, name, description, price, category, image_url)
VALUES
    (1, 'Cheeseburger', 'Juicy beef patty with cheese', 8.99, 'Main Course', 'https://groundbeefrecipes.com/wp-content/uploads/double-bacon-cheeseburger-recipe-6.jpg'),
    (2, 'French Fries', 'Crispy golden fries', 3.49, 'Appetizers', 'https://glutenfreecuppatea.co.uk/wp-content/uploads/2020/02/gluten-free-oven-baked-chips-recipe-2.jpg'),
    (3, 'Cola', 'Refreshing cold beverage', 1.99, 'Drinks', 'https://alkostore.ee/wp-content/uploads/img.0dab5f074d836615.jpg'),
    (4, 'Caesar Salad', 'Fresh romaine lettuce with Caesar dressing', 6.99, 'Appetizers', 'https://www.feastingathome.com/wp-content/uploads/2021/10/Caesar-salad_-4.jpg'),
    (5, 'Chocolate Cake', 'Rich chocolate layered cake', 4.99, 'Desserts', 'https://sallysbakingaddiction.com/wp-content/uploads/2013/04/triple-chocolate-cake-4.jpg'),
    (6, 'Grilled Chicken', 'Herb-marinated grilled chicken breast', 10.49, 'Main Course', 'https://images.themodernproper.com/production/posts/GrilledChicken_6.jpg?w=1200&q=82&auto=format&fit=crop&dm=1690299715&s=c5d804291abb28e0505eb29c251fd7f8'),
    (7, 'Spaghetti Bolognese', 'Classic Italian pasta with meat sauce', 9.99, 'Main Course', 'https://assets.sainsburys-groceries.co.uk/gol/linda-mccartneys-classic-spaghetti-bolognese/original.jpg'),
    (8, 'Garlic Bread', 'Toasted bread with garlic and butter', 2.99, 'Appetizers', 'https://img.taste.com.au/gZSLDi_o/taste/2016/11/easy-garlic-bread-97118-1.jpeg'),
    (9, 'Orange Juice', 'Freshly squeezed orange juice', 2.49, 'Drinks', 'https://www.alphafoodie.com/wp-content/uploads/2020/11/Orange-Ginger-Juice-2-of-2.jpeg'),
    (10, 'Mineral Water', 'Sparkling mineral water', 1.50, 'Drinks', 'https://cdn1.npcdn.net/images/c971b7923f44e63ac60c012d8f12205b_1658809995.webp?md5id=51783435ce8f5bb47f2c2bc7ebc29eb1&new_width=1000&new_height=1000&w=1700551537&from=jpeg'),
    (11, 'Ice Cream', 'Vanilla or chocolate ice cream', 3.50, 'Desserts', 'https://inthewildheartkitchen.com/wp-content/uploads/2023/06/pxl_20230630_151737983.portrait.jpg'),
    (12, 'Fruit Salad', 'Assorted seasonal fruits', 3.99, 'Desserts', 'https://cdn.apartmenttherapy.info/image/upload/f_jpg,q_auto:eco,c_fill,g_auto,w_1500,ar_1:1/k%2FPhoto%2FRecipes%2F2024-06-fruit-salad%2Ffruit-salad-0410-cropped');
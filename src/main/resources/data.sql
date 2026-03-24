-- Seed data for development

-- Store
MERGE INTO store (id, store_id, name) KEY(id) VALUES (1, 'store-001', '맛있는 식당');
MERGE INTO store (id, store_id, name) KEY(id) VALUES (2, 'store-002', '행복한 카페');

-- Admin: inserted via DataInitializer (bcrypt hash generated at runtime)

-- Tables
MERGE INTO store_table (id, store_id, table_number) KEY(id) VALUES (1, 1, 1);
MERGE INTO store_table (id, store_id, table_number) KEY(id) VALUES (2, 1, 2);
MERGE INTO store_table (id, store_id, table_number) KEY(id) VALUES (3, 1, 3);
MERGE INTO store_table (id, store_id, table_number) KEY(id) VALUES (4, 2, 1);
MERGE INTO store_table (id, store_id, table_number) KEY(id) VALUES (5, 2, 2);

-- Categories
MERGE INTO category (id, store_id, name, display_order) KEY(id) VALUES (1, 1, '메인 메뉴', 1);
MERGE INTO category (id, store_id, name, display_order) KEY(id) VALUES (2, 1, '사이드 메뉴', 2);
MERGE INTO category (id, store_id, name, display_order) KEY(id) VALUES (3, 1, '음료', 3);
MERGE INTO category (id, store_id, name, display_order) KEY(id) VALUES (4, 2, '커피', 1);
MERGE INTO category (id, store_id, name, display_order) KEY(id) VALUES (5, 2, '디저트', 2);

-- Menus
MERGE INTO menu (id, category_id, store_id, name, price, description, display_order) KEY(id) VALUES (1, 1, 1, '김치찌개', 9000, '돼지고기 김치찌개', 1);
MERGE INTO menu (id, category_id, store_id, name, price, description, display_order) KEY(id) VALUES (2, 1, 1, '된장찌개', 8000, '두부 된장찌개', 2);
MERGE INTO menu (id, category_id, store_id, name, price, description, display_order) KEY(id) VALUES (3, 2, 1, '계란말이', 5000, '치즈 계란말이', 1);
MERGE INTO menu (id, category_id, store_id, name, price, description, display_order) KEY(id) VALUES (4, 3, 1, '콜라', 2000, NULL, 1);
MERGE INTO menu (id, category_id, store_id, name, price, description, display_order) KEY(id) VALUES (5, 4, 2, '아메리카노', 4500, 'HOT/ICE', 1);
MERGE INTO menu (id, category_id, store_id, name, price, description, display_order) KEY(id) VALUES (6, 5, 2, '치즈케이크', 6000, '뉴욕 치즈케이크', 1);

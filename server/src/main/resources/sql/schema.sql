-- 1) Пользователи
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    password_hash CHAR(40) NOT NULL
    );

-- 2) Справочник единиц измерения
CREATE TABLE IF NOT EXISTS unit_of_measure (
    code VARCHAR(10) PRIMARY KEY,
    description TEXT
    );

-- Заполним базовые единицы
INSERT INTO unit_of_measure(code, description) VALUES
    ('METERS', 'Метры'),
    ('PCS',    'Штуки'),
    ('LITERS', 'Литры')
    ON CONFLICT (code) DO NOTHING;

-- 3) Организации (производители)
CREATE TABLE IF NOT EXISTS organizations (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    full_name TEXT,
    type TEXT CHECK (type IN ('COMMERCIAL','PUBLIC','GOVERNMENT','PRIVATE_LIMITED_COMPANY'))
    );

-- 4) Основная таблица продуктов
CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL,
    coord_x REAL NOT NULL,
    coord_y REAL NOT NULL,
    creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    price DOUBLE PRECISION NOT NULL CHECK (price > 0),
    unit VARCHAR(10) REFERENCES unit_of_measure(code),
    organization_id INT
    REFERENCES organizations(id),
    owner_id INT NOT NULL
    REFERENCES users(id)
    );

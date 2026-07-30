-- ==========================================
-- 1. ESQUEMAS, EXTENSIONES Y DROPS
-- ==========================================
CREATE SCHEMA IF NOT EXISTS auth;;
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";;

-- Drops ordenados considerando dependencias de esquemas cruzados
DROP TABLE IF EXISTS order_items CASCADE;;
DROP TABLE IF EXISTS orders CASCADE;;
DROP TABLE IF EXISTS products CASCADE;;
DROP TABLE IF EXISTS modelos CASCADE;;
DROP TABLE IF EXISTS marcas CASCADE;;
DROP TABLE IF EXISTS categories CASCADE;;
DROP TABLE IF EXISTS auth.usuarios_roles CASCADE;;
DROP TABLE IF EXISTS auth.usuarios CASCADE;;
DROP TABLE IF EXISTS auth.roles CASCADE;;
DROP TABLE IF EXISTS auth.permisos CASCADE;;
DROP TABLE IF EXISTS auth.roles_permisos CASCADE;;

-- ==========================================
-- 2. TABLAS (DDL) - MÓDULO AUTH
-- ==========================================
CREATE TABLE auth.usuarios (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               username VARCHAR(50) NOT NULL UNIQUE,
                               email VARCHAR(100) NOT NULL UNIQUE,
                               password VARCHAR(255) NOT NULL,
                               rol VARCHAR(20) NOT NULL DEFAULT 'ROLE_USER',
                               estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
                               creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);;

CREATE INDEX idx_usuarios_username ON auth.usuarios(username);;

-- Tabla: roles (Catálogo de Roles del sistema)
CREATE TABLE auth.roles (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            nombre VARCHAR(50) NOT NULL UNIQUE,
                            descripcion TEXT,
                            creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);;

-- Tabla de Permisos
CREATE TABLE auth.permisos (
                               id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               nombre VARCHAR(100) NOT NULL UNIQUE,
                               descripcion TEXT,
                               creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);;

-- Tabla Intermedia: Roles y Permisos (Muchos a Muchos)
CREATE TABLE auth.roles_permisos (
                                     rol_id UUID NOT NULL REFERENCES auth.roles(id) ON DELETE CASCADE,
                                     permiso_id UUID NOT NULL REFERENCES auth.permisos(id) ON DELETE CASCADE,
                                     creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     PRIMARY KEY (rol_id, permiso_id)
);;

-- Tabla Intermedia: Usuarios y Roles (Muchos a Muchos)
CREATE TABLE auth.usuarios_roles (
                                     usuario_id UUID NOT NULL REFERENCES auth.usuarios(id) ON DELETE CASCADE,
                                     rol_id UUID NOT NULL REFERENCES auth.roles(id) ON DELETE CASCADE,
                                     creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     PRIMARY KEY (usuario_id, rol_id)
);;

CREATE INDEX idx_roles_permisos_rol ON auth.roles_permisos(rol_id);;
CREATE INDEX idx_roles_permisos_permiso ON auth.roles_permisos(permiso_id);;
CREATE INDEX idx_usuarios_roles_usuario ON auth.usuarios_roles(usuario_id);;
CREATE INDEX idx_usuarios_roles_rol ON auth.usuarios_roles(rol_id);;

CREATE INDEX idx_permisos_nombre ON auth.permisos(nombre);;
CREATE INDEX idx_roles_nombre ON auth.roles(nombre);;

-- ==========================================
-- 2.1. TABLAS (DDL) - NEGOCIO / PUBLIC
-- ==========================================

-- Tabla: marcas (Catálogo de marcas de autos)
CREATE TABLE marcas (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        nombre VARCHAR(100) NOT NULL UNIQUE,
                        imagen VARCHAR(500),
                        pais_origen VARCHAR(100) NOT NULL,
                        creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);;

CREATE INDEX idx_marcas_nombre ON marcas(nombre);;

-- Tabla: modelos (Catálogo de modelos pertenecientes a una marca)
CREATE TABLE modelos (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         marca_id UUID NOT NULL REFERENCES marcas(id) ON DELETE CASCADE,
                         nombre VARCHAR(100) NOT NULL,
                         descripcion TEXT,
                         anio_lanzamiento INT NOT NULL,
                         creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);;

CREATE INDEX idx_modelos_marca_id ON modelos(marca_id);;
CREATE INDEX idx_modelos_nombre ON modelos(nombre);;

-- Tabla: categories (Catálogo de categorías de productos)
CREATE TABLE categories (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            nombre VARCHAR(100) NOT NULL UNIQUE,
                            descripcion TEXT,
                            imagen VARCHAR(500),
                            creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);;

CREATE INDEX idx_categories_nombre ON categories(nombre);;

-- Tabla: products (Catálogo de repuestos y equipos vinculado a categorías y modelos)
CREATE TABLE products (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          modelo_id UUID REFERENCES modelos(id) ON DELETE SET NULL,
                          category_id UUID REFERENCES categories(id) ON DELETE SET NULL,
                          title VARCHAR(255) NOT NULL,
                          price DECIMAL(10, 2) NOT NULL,
                          stock INT NOT NULL DEFAULT 0,
                          image VARCHAR(500),
                          description TEXT,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);;

CREATE INDEX idx_products_modelo_id ON products(modelo_id);;
CREATE INDEX idx_products_category_id ON products(category_id);;
CREATE INDEX idx_products_title ON products(title);;

-- Tabla: orders (Órdenes de compra con referencia cruzada al esquema auth)
CREATE TABLE orders (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        user_id UUID REFERENCES auth.usuarios(id) ON DELETE SET NULL,
                        total_amount DECIMAL(10, 2) NOT NULL,
                        status VARCHAR(30) NOT NULL DEFAULT 'pending',
                        shipping_address TEXT NOT NULL,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);;

-- Tabla: order_items (Detalle de ítems por orden)
CREATE TABLE order_items (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                             order_id UUID NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
                             product_id UUID NOT NULL REFERENCES products(id),
                             quantity INT NOT NULL,
                             price_at_purchase DECIMAL(10, 2) NOT NULL
);;

-- ==========================================
-- 3. STORED PROCEDURES / FUNCIONES (PL/pgSQL)
-- ==========================================

DROP FUNCTION IF EXISTS sp_registrar_usuario(VARCHAR, VARCHAR, VARCHAR, VARCHAR);;
DROP FUNCTION IF EXISTS sp_registrar_usuario(VARCHAR, VARCHAR, VARCHAR, UUID);;
DROP FUNCTION IF EXISTS sp_obtener_usuario_por_username(VARCHAR);;
DROP FUNCTION IF EXISTS sp_obtener_marcas();;
DROP FUNCTION IF EXISTS sp_registrar_marca(VARCHAR, VARCHAR, VARCHAR);;
DROP FUNCTION IF EXISTS sp_registrar_modelo(UUID, VARCHAR, TEXT, INT);;

-- SP para Registrar Usuario apuntando al esquema auth
CREATE OR REPLACE FUNCTION sp_registrar_usuario(
    p_username VARCHAR,
    p_email VARCHAR,
    p_password VARCHAR,
    p_rol_id UUID
) RETURNS UUID AS $$
DECLARE
v_usuario_id UUID;
    v_nombre_rol VARCHAR;
BEGIN
    -- 1. Obtener el nombre del rol del esquema auth
SELECT nombre INTO v_nombre_rol FROM auth.roles WHERE id = p_rol_id;

IF v_nombre_rol IS NULL THEN
        RAISE EXCEPTION 'El rol con ID % no existe', p_rol_id;
END IF;

    -- 2. Insertar en auth.usuarios
INSERT INTO auth.usuarios (username, email, password, rol)
VALUES (p_username, p_email, p_password, v_nombre_rol)
    RETURNING id INTO v_usuario_id;

-- 3. Insertar en auth.usuarios_roles
INSERT INTO auth.usuarios_roles (usuario_id, rol_id)
VALUES (v_usuario_id, p_rol_id);

RETURN v_usuario_id;
END;
$$ LANGUAGE plpgsql;;

-- SP para Buscar Usuario por Username apuntando al esquema auth
CREATE OR REPLACE FUNCTION sp_obtener_usuario_por_username(
    p_username VARCHAR
) RETURNS TABLE (
    id UUID,
    username VARCHAR,
    email VARCHAR,
    password VARCHAR,
    rol VARCHAR,
    estado VARCHAR,
    permisos TEXT[]
) AS $$
BEGIN
RETURN QUERY
SELECT
    u.id,
    u.username,
    u.email,
    u.password,
    u.rol,
    u.estado,
    COALESCE(ARRAY_AGG(DISTINCT p.nombre::TEXT) FILTER (WHERE p.nombre IS NOT NULL), ARRAY[]::TEXT[]) AS permisos
FROM auth.usuarios u
         LEFT JOIN auth.usuarios_roles ur ON u.id = ur.usuario_id
         LEFT JOIN auth.roles_permisos rp ON ur.rol_id = rp.rol_id
         LEFT JOIN auth.permisos p ON rp.permiso_id = p.id
WHERE u.username = p_username AND u.estado = 'ACTIVO'
GROUP BY u.id, u.username, u.email, u.password, u.rol, u.estado;
END;
$$ LANGUAGE plpgsql;;

-- SP para Listar Marcas
CREATE OR REPLACE FUNCTION sp_obtener_marcas()
RETURNS TABLE (
    id UUID,
    nombre VARCHAR,
    imagen VARCHAR,
    pais_origen VARCHAR
) AS $$
BEGIN
RETURN QUERY
SELECT m.id, m.nombre, m.imagen, m.pais_origen
FROM marcas m
ORDER BY m.nombre ASC;
END;
$$ LANGUAGE plpgsql;;

-- SP para Registrar Marca
CREATE OR REPLACE FUNCTION sp_registrar_marca(
    p_nombre VARCHAR,
    p_imagen VARCHAR,
    p_pais_origen VARCHAR
) RETURNS UUID AS $$
DECLARE
v_id UUID;
BEGIN
INSERT INTO marcas (nombre, imagen, pais_origen)
VALUES (p_nombre, p_imagen, p_pais_origen)
    RETURNING id INTO v_id;

RETURN v_id;
END;
$$ LANGUAGE plpgsql;;

-- SP para Registrar Modelo
CREATE OR REPLACE FUNCTION sp_registrar_modelo(
    p_marca_id UUID,
    p_nombre VARCHAR,
    p_descripcion TEXT,
    p_anio_lanzamiento INT
) RETURNS UUID AS $$
DECLARE
v_id UUID;
BEGIN
INSERT INTO modelos (marca_id, nombre, descripcion, anio_lanzamiento)
VALUES (p_marca_id, p_nombre, p_descripcion, p_anio_lanzamiento)
    RETURNING id INTO v_id;

RETURN v_id;
END;
$$ LANGUAGE plpgsql;;

-- ==========================================
-- 4. DATOS INICIALES DE PRUEBA (DML)
-- ==========================================
INSERT INTO auth.usuarios (username, email, password, rol)
VALUES ('admin', 'admin@empresa.com', '$2a$10$E2UPv7arXnm918P10lO.2OHY8Lxe8v2V4a9R3Xm5a3D2C1B0A9Z8y', 'ROLE_ADMIN');;
INSERT INTO auth.usuarios (username, email, password, rol)
VALUES ('daniel', 'daniel@empresa.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_ADMIN');;

-- Datos iniciales de prueba para marcas
INSERT INTO marcas (nombre, imagen, pais_origen)
VALUES
    ('Toyota', 'https://ejemplo.com/toyota.png', 'Japón'),
    ('Chevrolet', 'https://ejemplo.com/chevrolet.png', 'Estados Unidos'),
    ('Renault', 'https://ejemplo.com/renault.png', 'Francia');;

-- Datos iniciales de prueba para roles
INSERT INTO auth.roles (nombre, descripcion) VALUES
                                                 ('ROLE_ADMIN', 'Administrador con acceso total al sistema'),
                                                 ('ROLE_USER', 'Usuario estándar del sistema');;

-- Inserción de todos los permisos CRUD (Productos, Categorías, Marcas y Modelos)
INSERT INTO auth.permisos (nombre, descripcion) VALUES
                                                    ('PRODUCTS_READ', 'Consultar y listar productos'),
                                                    ('PRODUCTS_CREATE', 'Crear nuevos productos'),
                                                    ('PRODUCTS_UPDATE', 'Actualizar productos existentes'),
                                                    ('PRODUCTS_DELETE', 'Eliminar productos'),
                                                    ('CATEGORIES_READ', 'Consultar y listar categorías de productos'),
                                                    ('CATEGORIES_CREATE', 'Crear nuevas categorías de productos'),
                                                    ('CATEGORIES_UPDATE', 'Actualizar categorías existentes'),
                                                    ('CATEGORIES_DELETE', 'Eliminar categorías de productos'),
                                                    ('BRANDS_READ', 'Consultar y listar marcas de vehículos'),
                                                    ('BRANDS_CREATE', 'Crear nuevas marcas de vehículos'),
                                                    ('BRANDS_UPDATE', 'Actualizar marcas existentes'),
                                                    ('BRANDS_DELETE', 'Eliminar marcas de vehículos'),
                                                    ('MODELS_READ', 'Consultar y listar modelos de vehículos'),
                                                    ('MODELS_CREATE', 'Crear nuevos modelos de vehículos'),
                                                    ('MODELS_UPDATE', 'Actualizar modelos existentes'),
                                                    ('MODELS_DELETE', 'Eliminar modelos de vehículos')
    ON CONFLICT (nombre) DO UPDATE
                                SET descripcion = EXCLUDED.descripcion;;

-- Limpiar asociaciones previas de roles_permisos
DELETE FROM auth.roles_permisos;;

-- Asignar permisos a ROLE_ADMIN
INSERT INTO auth.roles_permisos (rol_id, permiso_id)
SELECT r.id, p.id
FROM auth.roles r, auth.permisos p
WHERE r.nombre = 'ROLE_ADMIN'
    ON CONFLICT (rol_id, permiso_id) DO NOTHING;;

-- Asignar permisos de lectura a ROLE_USER
INSERT INTO auth.roles_permisos (rol_id, permiso_id)
SELECT r.id, p.id
FROM auth.roles r, auth.permisos p
WHERE r.nombre = 'ROLE_USER'
  AND p.nombre IN ('PRODUCTS_READ', 'CATEGORIES_READ', 'BRANDS_READ', 'MODELS_READ')
    ON CONFLICT (rol_id, permiso_id) DO NOTHING;;

-- Vincular usuarios iniciales con el módulo usuarios_roles
INSERT INTO auth.usuarios_roles (usuario_id, rol_id)
SELECT u.id, r.id
FROM auth.usuarios u, auth.roles r
WHERE u.username IN ('admin', 'daniel') AND r.nombre = 'ROLE_ADMIN';;

-- Datos iniciales de prueba para modelos
INSERT INTO modelos (marca_id, nombre, descripcion, anio_lanzamiento)
SELECT id, 'Hilux', 'Camioneta pickup 4x4 diésel', 2023 FROM marcas WHERE nombre = 'Toyota';;

INSERT INTO modelos (marca_id, nombre, descripcion, anio_lanzamiento)
SELECT id, 'Corolla', 'Sedán compacto de alta eficiencia', 2022 FROM marcas WHERE nombre = 'Toyota';;

INSERT INTO modelos (marca_id, nombre, descripcion, anio_lanzamiento)
SELECT id, 'Aveo', 'Vehículo compacto económico', 2021 FROM marcas WHERE nombre = 'Chevrolet';;

INSERT INTO modelos (marca_id, nombre, descripcion, anio_lanzamiento)
SELECT id, 'Twingo', 'Hatchback urbano', 2008 FROM marcas WHERE nombre = 'Renault';;

-- Datos iniciales de prueba para categorías
INSERT INTO categories (nombre, descripcion, imagen)
VALUES
    ('Repuestos Automotrices', 'Partes y repuestos originales y alternativos', 'https://ejemplo.com/repuestos.png'),
    ('Herramientas', 'Equipos de diagnóstico y herramientas mecánicas', 'https://ejemplo.com/herramientas.png');;

-- Datos iniciales de prueba para products asociados a modelos y categorías
INSERT INTO products (modelo_id, category_id, title, price, stock, description)
SELECT m.id, c.id, 'Filtro de Aceite Twingo', 15.50, 25, 'Filtro de aceite original compatible con motores 16 válvulas.'
FROM modelos m, categories c
WHERE m.nombre = 'Twingo' AND c.nombre = 'Repuestos Automotrices';;

INSERT INTO products (modelo_id, category_id, title, price, stock, description)
SELECT m.id, c.id, 'Kit de Embrague Hilux', 120.00, 8, 'Kit de embrague reforzado para alto rendimiento 4x4.'
FROM modelos m, categories c
WHERE m.nombre = 'Hilux' AND c.nombre = 'Repuestos Automotrices';;

-- Producto sin modelo específico
INSERT INTO products (modelo_id, category_id, title, price, stock, description)
SELECT NULL, c.id, 'Escáner Automotriz OBD2', 45.00, 15, 'Herramienta de diagnóstico multimarca para vehículos modernos.'
FROM categories c
WHERE c.nombre = 'Herramientas';;
-- Mapuescuela - script para MySQL Workbench
-- Ejecutar todo el archivo (rayo) o sentencia por sentencia.

CREATE DATABASE IF NOT EXISTS mapuescuela
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE mapuescuela;

CREATE TABLE IF NOT EXISTS producto (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre      VARCHAR(150) NOT NULL,
    descripcion VARCHAR(500) NULL,
    categoria   VARCHAR(100) NULL,
    precio      INT NOT NULL,
    fotografia  VARCHAR(255) NULL,
    stock       INT NOT NULL DEFAULT 0,
    estado      VARCHAR(50)  NOT NULL
);

CREATE TABLE IF NOT EXISTS cliente (
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre    VARCHAR(150) NOT NULL,
    correo    VARCHAR(150) NOT NULL,
    telefono  VARCHAR(30)  NULL,
    direccion VARCHAR(255) NULL
);

CREATE TABLE IF NOT EXISTS pedido (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id          BIGINT       NOT NULL,
    fecha               DATETIME     NOT NULL,
    total               INT          NOT NULL,
    modalidad_entrega   VARCHAR(50)  NOT NULL,
    estado              VARCHAR(50)  NOT NULL,
    process_instance_id VARCHAR(64)  NULL,
    CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (cliente_id) REFERENCES cliente (id)
);

CREATE TABLE IF NOT EXISTS detalle_pedido (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id   BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad    INT    NOT NULL,
    precio      INT    NOT NULL,
    CONSTRAINT fk_detalle_pedido
        FOREIGN KEY (pedido_id) REFERENCES pedido (id),
    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (producto_id) REFERENCES producto (id)
);

CREATE TABLE IF NOT EXISTS comprobante (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id    BIGINT       NOT NULL,
    archivo      VARCHAR(255) NOT NULL,
    fecha_subida DATETIME     NOT NULL,
    estado       VARCHAR(50)  NOT NULL,
    CONSTRAINT fk_comprobante_pedido
        FOREIGN KEY (pedido_id) REFERENCES pedido (id)
);

CREATE TABLE IF NOT EXISTS despacho (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id          BIGINT       NOT NULL,
    empresa_transporte VARCHAR(150) NOT NULL,
    numero_seguimiento VARCHAR(100) NOT NULL,
    fecha_envio        DATETIME     NOT NULL,
    CONSTRAINT fk_despacho_pedido
        FOREIGN KEY (pedido_id) REFERENCES pedido (id)
);

-- Datos de prueba para Postman (cliente 1 y producto 1)
INSERT INTO cliente (nombre, correo, telefono, direccion)
SELECT 'Juan Perez', 'juan.perez@mail.com', '912345678', 'Av. Principal 123'
WHERE NOT EXISTS (SELECT 1 FROM cliente WHERE correo = 'juan.perez@mail.com');

INSERT INTO producto (nombre, descripcion, categoria, precio, fotografia, stock, estado)
SELECT 'Libro usado', 'Libro en buen estado', 'Libros', 5000, NULL, 1, 'Disponible'
WHERE NOT EXISTS (SELECT 1 FROM producto WHERE nombre = 'Libro usado');

-- =====================================================
-- Plazoleta de comidas - Base de datos (MySQL 8)
-- Basado en el script de la profe, ajustado a las HU 01 a 18
-- =====================================================

CREATE DATABASE IF NOT EXISTS plazoleta_db;
USE plazoleta_db;

-- TABLA ROL (HU-01, HU-06, HU-08: cada usuario queda con un rol)
CREATE TABLE rol (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(100)
);

-- TABLA USUARIO (HU-01 propietario, HU-06 empleado, HU-08 cliente)
-- clave de 255 porque el hash de bcrypt mide 60 caracteres
-- fechaDeNacimiento puede ir vacia: solo la pide la HU-01
CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    apellido VARCHAR(50) NOT NULL,
    documentoDeIdentidad VARCHAR(50) NOT NULL UNIQUE,
    celular VARCHAR(13) NOT NULL,
    fechaDeNacimiento DATE NULL,
    correo VARCHAR(50) NOT NULL UNIQUE,
    clave VARCHAR(255) NOT NULL,
    idRol INT NOT NULL,

    CONSTRAINT fk_usuario_rol
        FOREIGN KEY (idRol)
        REFERENCES rol(id)
);

-- TABLA RESTAURANTE (HU-02)
CREATE TABLE restaurante (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    nit VARCHAR(50) NOT NULL UNIQUE,
    direccion VARCHAR(100) NOT NULL,
    telefono VARCHAR(13) NOT NULL,
    urlLogo VARCHAR(200) NOT NULL,
    idPropietario INT NOT NULL,

    CONSTRAINT fk_restaurante_propietario
        FOREIGN KEY (idPropietario)
        REFERENCES usuario(id)
);

-- TABLA CATEGORIA (HU-03 categoria del plato, HU-10 filtro)
CREATE TABLE categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(100)
);

-- TABLA PLATO (HU-03, HU-04, HU-07, HU-10)
-- precio entero y estado (activo) en TRUE por defecto
CREATE TABLE plato (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    precio INT NOT NULL,
    descripcion VARCHAR(100) NOT NULL,
    urlImagen VARCHAR(200) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    idCategoria INT NOT NULL,
    idRestaurante INT NOT NULL,

    CONSTRAINT fk_plato_categoria
        FOREIGN KEY (idCategoria)
        REFERENCES categoria(id),

    CONSTRAINT fk_plato_restaurante
        FOREIGN KEY (idRestaurante)
        REFERENCES restaurante(id)
);

-- TABLA EMPLEADO_RESTAURANTE (HU-06, HU-12, HU-13)
-- idEmpleado UNIQUE: un empleado pertenece a un solo restaurante
CREATE TABLE empleado_restaurante (
    id INT AUTO_INCREMENT PRIMARY KEY,
    idRestaurante INT NOT NULL,
    idEmpleado INT NOT NULL UNIQUE,

    CONSTRAINT fk_emp_rest_restaurante
        FOREIGN KEY (idRestaurante)
        REFERENCES restaurante(id),

    CONSTRAINT fk_emp_rest_empleado
        FOREIGN KEY (idEmpleado)
        REFERENCES usuario(id)
);

-- TABLA PEDIDO (HU-11 a HU-16)
-- estado: PENDIENTE, EN_PREPARACION, LISTO, ENTREGADO, CANCELADO
-- idEmpleado vacio hasta que un empleado se asigna (HU-13)
-- pin: se envia al cliente (HU-14) y se pide para entregar (HU-15)
CREATE TABLE pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    pin VARCHAR(10) NULL,
    idEmpleado INT NULL,
    idCliente INT NOT NULL,
    idRestaurante INT NOT NULL,

    CONSTRAINT fk_pedido_empleado
        FOREIGN KEY (idEmpleado)
        REFERENCES usuario(id),

    CONSTRAINT fk_pedido_cliente
        FOREIGN KEY (idCliente)
        REFERENCES usuario(id),

    CONSTRAINT fk_pedido_restaurante
        FOREIGN KEY (idRestaurante)
        REFERENCES restaurante(id)
);

-- TABLA PLATO_PEDIDO (HU-11: platos del pedido y cantidad de cada uno)
CREATE TABLE plato_pedido (
    id INT AUTO_INCREMENT PRIMARY KEY,
    cantidad INT NOT NULL,
    idPedido INT NOT NULL,
    idPlato INT NOT NULL,

    CONSTRAINT fk_plato_pedido_pedido
        FOREIGN KEY (idPedido)
        REFERENCES pedido(id),

    CONSTRAINT fk_plato_pedido_plato
        FOREIGN KEY (idPlato)
        REFERENCES plato(id)
);

-- TABLA TRAZABILIDAD (HU-17: un registro por cada cambio de estado)
-- HU-18 calcula los tiempos con la columna fecha
CREATE TABLE trazabilidad (
    id INT AUTO_INCREMENT PRIMARY KEY,
    idPedido INT NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_trazabilidad_pedido
        FOREIGN KEY (idPedido)
        REFERENCES pedido(id)
);

-- =====================================================
-- DATOS INICIALES
-- =====================================================

-- Los 4 roles del sistema
INSERT INTO rol (nombre, descripcion) VALUES
    ('ADMINISTRADOR', 'Crea propietarios y restaurantes'),
    ('PROPIETARIO', 'Gestiona platos y empleados de su restaurante'),
    ('EMPLEADO', 'Atiende los pedidos del restaurante'),
    ('CLIENTE', 'Realiza pedidos');

-- Administrador inicial: PENDIENTE (falta el hash bcrypt de su clave)

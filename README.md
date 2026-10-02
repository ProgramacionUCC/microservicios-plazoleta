# Plazoleta de Comidas — Spring Boot

Proyecto de plazoleta donde cada parte funciona por separado y se hablan entre sí. La idea es que el sistema crezca sin enredarse.

> Este README cuenta lo que **ya está hecho en el código**. Se va actualizando en cada Historia de Usuario. Para el plan completo ver `CONTEXTO_PROYECTO.md`.

## Qué hace el proyecto

Una plazoleta con restaurantes. Un administrador registra a los dueños de los restaurantes, esos dueños manejan su local y sus platos, y los clientes hacen pedidos que pasan por varios estados hasta entregarse.

El sistema se divide en 4 partes (microservicios), organizadas dentro de un solo proyecto:

| Parte | Qué resuelve |
|-------|--------------|
| **Usuarios** | Crear cuentas, login, claves con bcrypt y permisos por rol |
| **Plazoleta** | Restaurantes, platos y pedidos |
| **Trazabilidad** | Estados del pedido e historial |
| **Notificaciones** | Aviso al cliente con PIN cuando el pedido está listo |

## Migración a Spring Boot

En el sprint 1 el proyecto se hizo en **Java puro** (datos en listas en memoria y pruebas con `Main.java`). Ahora se pasó a **Spring Boot**:

- Los datos se guardan en **MySQL**, ya no se pierden al cerrar.
- Cada funcionalidad es un **endpoint** que se prueba desde Postman.
- Las validaciones de formato se hacen con **anotaciones** en los DTO (`@NotBlank`, `@Email`, `@Pattern`...), en vez de muchos `if`.
- El código de Java puro sigue guardado en el historial de Git (ramas del sprint 1 y `main`).

## Cómo correr el proyecto

1. Tener **Java 25** y **MySQL 8** (usuario `root`, clave `root`).
2. Correr el script de la base de datos `docs/script.sql` en MySQL (crea `plazoleta_db`, todas las tablas y los 4 roles).
3. Abrir el proyecto en IntelliJ como proyecto **Maven** y correr `PlazoletaApplication`.
   O desde la terminal: `./mvnw spring-boot:run`
4. El proyecto queda en `http://localhost:8080`.

## Cómo está organizado el código

```
src/main/java/com/plazoleta/
  PlazoletaApplication.java   → arranca el proyecto (reemplaza al Main.java)
  controller/                 → las puertas de entrada: reciben la petición y responden
  dto/request/                → lo que llega en la petición, con sus reglas de formato
  dto/response/               → lo que se devuelve (nunca la clave)
  entity/                     → las fichas que se guardan en las tablas de MySQL
  repository/                 → guardan y buscan en MySQL (Spring hace el trabajo)
  service/ + service/impl/    → las reglas de negocio de cada HU
  exception/                  → convierte los errores en mensajes claros
  security/                   → bcrypt para las claves (y el login en HU-05)
src/main/resources/
  application.properties      → conexión a MySQL
docs/script.sql               → base de datos completa (HU-01 a HU-18)
```

- **dto** revisa que los datos vengan bien escritos.
- **service** piensa y decide si se cumplen las reglas de la HU.
- **repository** solo guarda y busca, no pregunta nada.

## Base de datos

El script `docs/script.sql` crea de una vez todas las tablas que necesitan las HU del proyecto:

| Tabla | Para qué | HU |
|-------|----------|----|
| `rol` | Los 4 roles: ADMINISTRADOR, PROPIETARIO, EMPLEADO, CLIENTE | 01, 06, 08 |
| `usuario` | Todas las personas; lo que cambia es el rol | 01, 06, 08 |
| `restaurante` | Restaurantes y su propietario | 02 |
| `categoria` | Categoría de cada plato | 03, 10 |
| `plato` | Platos del menú (precio entero, activo por defecto) | 03, 04, 07, 10 |
| `empleado_restaurante` | A qué restaurante pertenece cada empleado | 06, 12, 13 |
| `pedido` | Pedidos, su estado y el PIN de entrega | 11 a 16 |
| `plato_pedido` | Platos de cada pedido y su cantidad | 11 |
| `trazabilidad` | Cada cambio de estado de un pedido con su fecha | 17, 18 |

## HU-01 Crear propietario

### Qué hace
Permite crear la cuenta de un propietario. Es el primer paso: sin propietario no se pueden crear restaurantes después.

**Endpoint:** `POST /api/v1/usuarios/propietario`

Ejemplo de lo que se envía:
```json
{
  "nombre": "Carlos",
  "apellido": "Perez",
  "documentoDeIdentidad": "12345678",
  "celular": "+573005698325",
  "fechaDeNacimiento": "1990-05-10",
  "correo": "carlos@mail.com",
  "clave": "clave123"
}
```

### Cómo lo hace
1. **El controller** (`UsuarioController`) recibe la petición. Con `@Valid` pide revisar el DTO antes de seguir.
2. **El DTO** (`PropietarioRequestDTO`) revisa el formato con anotaciones:
   - Todos los campos son obligatorios.
   - El correo debe tener forma de correo.
   - El celular máximo 13 caracteres, solo números y `+` al inicio.
   - El documento solo números.
3. **El service** (`UsuarioServiceImpl`) revisa las reglas en 4 pasos:
   - Que el correo y el documento no estén ya registrados.
   - Que sea mayor de edad (18 años o más).
   - Le pone el rol `PROPIETARIO`.
   - Encripta la clave con bcrypt y lo guarda en MySQL.
4. **Si algo falla**, `GlobalExceptionHandler` responde un mensaje claro con código 400.

### Respuestas
| Caso | Respuesta |
|------|-----------|
| Todo correcto | `201` con los datos del propietario y su rol (sin la clave) |
| Correo repetido | `400` `{"mensaje": "El correo ya esta registrado"}` |
| Menor de edad | `400` `{"mensaje": "El propietario debe ser mayor de edad"}` |
| Formato malo | `400` con cada campo y su error, ej: `{"correo": "El correo no es valido"}` |

### Para qué se hace así
El formato se revisa en el DTO con anotaciones porque es más corto y fácil de leer. Las reglas que necesitan la base de datos o la fecha actual (correo repetido, mayoría de edad) quedan en el service. La clave nunca se guarda ni se devuelve en texto normal.

> La regla "solo el administrador puede crear propietarios" se agrega en la HU-05 (login y permisos).

## Estado actual

| HU | Estado |
|----|--------|
| HU-01 Crear propietario | ✅ Migrada a Spring Boot |
| HU-02 Crear restaurante | En migración |
| HU-03 Crear plato | En migración |
| HU-04 Modificar plato | En migración |
| HU-05 Autenticación | En migración |

**Qué sigue:** migrar HU-02 a HU-05 y luego el sprint 2 (HU-06 a HU-12). Cada HU nueva agrega su parte aquí en este README.

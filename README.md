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

> Desde la HU-05 este endpoint exige haber iniciado sesión como **ADMINISTRADOR**.

## HU-02 Crear restaurante

### Qué hace
Permite que el administrador cree un restaurante y lo asocie a un propietario que ya existe.

**Endpoint:** `POST /api/v1/restaurantes` (solo ADMINISTRADOR)

```json
{
  "nombre": "El Corral",
  "nit": "900123",
  "direccion": "Calle 1 # 2-3",
  "telefono": "+573001112233",
  "urlLogo": "http://logo.png",
  "idPropietario": 1
}
```

### Cómo lo hace
1. **El DTO** (`RestauranteRequestDTO`) revisa: todos los campos obligatorios, el nombre no puede ser solo números, el NIT solo números, el teléfono máximo 13 con `+` al inicio.
2. **El service** (`RestauranteServiceImpl`) revisa que el NIT no esté repetido y que el `idPropietario` sea de un usuario que existe y tiene rol `PROPIETARIO`. Si todo está bien, lo guarda.

### Para qué se hace así
Para que no se creen restaurantes sin dueño o con un usuario que no es propietario.

## HU-03 Crear plato

### Qué hace
Permite que el propietario cree platos en **su** restaurante. Cada plato pertenece a una categoría y nace activo.

**Endpoint:** `POST /api/v1/platos` (solo PROPIETARIO dueño del restaurante)

```json
{
  "nombre": "Corral Clasica",
  "precio": 25000,
  "descripcion": "Carne y queso",
  "urlImagen": "http://img.png",
  "idCategoria": 1,
  "idRestaurante": 1
}
```

Las categorías se crean y consultan en `POST /api/v1/categorias` y `GET /api/v1/categorias` (como en el proyecto de la profe).

### Cómo lo hace
1. **El DTO** (`PlatoRequestDTO`) revisa: campos obligatorios y precio entero mayor a 0.
2. **El service** (`PlatoServiceImpl`) revisa que el restaurante y la categoría existan y que el usuario logueado sea el dueño del restaurante.
3. Guarda el plato con `estado = true` (activo).

## HU-04 Modificar plato

### Qué hace
Permite que el propietario cambie **solo el precio y la descripción** de un plato de su restaurante.

**Endpoint:** `PATCH /api/v1/platos/{idPlato}` (solo PROPIETARIO dueño del restaurante)

```json
{
  "precio": 30000,
  "descripcion": "Carne, queso y tocineta"
}
```

### Cómo lo hace
1. **El DTO** (`ModificarPlatoRequestDTO`) solo tiene precio y descripción: los demás campos no se pueden mandar.
2. **El service** revisa que el plato exista y que el usuario logueado sea el dueño del restaurante del plato. Si es de otro restaurante responde `403`.

## HU-05 Agregar autenticación

### Qué hace
Todos los usuarios inician sesión con correo y clave. Al entrar reciben un **token** que deben enviar en cada petición. Cada endpoint solo lo puede usar el rol que corresponde.

**Endpoint:** `POST /api/v1/auth/login` (libre)

```json
{ "correo": "admin@plazoleta.com", "clave": "..." }
```

Responde `{ "token": "eyJ..." }`. Ese token se manda en las demás peticiones en el header:
`Authorization: Bearer eyJ...`

El script de la base de datos trae un **administrador inicial** (el correo y la clave de prueba están en el comentario de `docs/script.sql`).

### Cómo lo hace
1. **`AuthServiceImpl`** busca el usuario por correo y compara la clave con la guardada en bcrypt. Si falla responde `401` con "Usuario no encontrado" o "Clave incorrecta". Los intentos son ilimitados.
2. **`JwtService`** crea el token con el correo y el rol del usuario (dura 1 hora).
3. **`JwtAuthenticationFilter`** lee el token en cada petición y deja al usuario como logueado con su rol.
4. **`SecurityConfig`** dice quién puede usar cada endpoint:

| Endpoint | Quién |
|----------|-------|
| `POST /api/v1/auth/login` | Cualquiera |
| `POST /api/v1/usuarios/propietario` | ADMINISTRADOR |
| `POST /api/v1/restaurantes` | ADMINISTRADOR |
| `POST /api/v1/platos` | PROPIETARIO (dueño del restaurante) |
| `PATCH /api/v1/platos/{id}` | PROPIETARIO (dueño del restaurante) |
| `PATCH /api/v1/platos/{idPlato}/estado` | PROPIETARIO (dueño del restaurante) — HU-07 |
| Todo lo demás | Cualquier usuario logueado |

5. Que el propietario sea **el dueño** del restaurante lo revisa `PlatoServiceImpl` comparando el correo del token con el del propietario. En la HU-07 se verifica contra el dueño del restaurante **del plato** (no el que llega en el body): si no coincide responde `403`.

### Respuestas de seguridad
| Caso | Respuesta |
|------|-----------|
| Sin token o token alterado/vencido | `401` |
| Logueado pero con un rol sin permiso | `403` |
| Propietario intentando tocar platos de otro restaurante | `403` |

> La validación de "crear empleado (solo propietario)" se agrega cuando se haga la HU-06 (Crear cuenta empleado), porque es ahí donde se crea ese endpoint.

## HU-07 Habilitar/Deshabilitar plato

### Qué hace
Permite que el propietario active o desactive un plato de su restaurante para dejar de ofrecerlo sin borrarlo. Solo cambia el estado; el resto del plato queda igual.

**Endpoint:** `PATCH /api/v1/platos/{idPlato}/estado` (solo PROPIETARIO dueño del restaurante, con Bearer Token JWT)

```json
{
  "activo": false
}
```

### Cómo lo hace
1. **`SecurityConfig`** deja pasar solo a usuarios con rol `PROPIETARIO`. Sin token o con token inválido/vencido responde `401`.
2. **El DTO** (`HabilitarPlatoRequestDTO`) solo tiene el campo `activo` (`Boolean` obligatorio): los demás campos no se pueden mandar. Si el body viene mal responde `400`.
3. **El service** (`PlatoServiceImpl.cambiarEstadoPlato`) busca el plato por `idPlato`; si no existe responde `404`. Luego reutiliza `validarDueno` y compara el correo del token con el del propietario del restaurante del plato; si no es el dueño responde `403`.
4. Guarda el valor de `activo` en `Plato.estado` y devuelve el plato actualizado (`200`).

### Respuestas
| Caso | Respuesta |
|------|-----------|
| Todo correcto | `200` con el plato y su nuevo `estado` |
| Plato que no existe | `404` `{"mensaje": "El plato no existe"}` |
| Propietario de otro restaurante | `403` (no es el dueño del restaurante del plato) |
| Logueado con rol sin permiso | `403` |
| Sin token o token inválido/vencido | `401` |
| Body sin `activo` o con formato malo | `400` con el error del campo |

### Para qué se hace así
El campo se llama `activo` en la petición porque así lo pide la HU, y se guarda en `Plato.estado` porque esa es la columna que ya existe en la base de datos (no se crea columna nueva). La validación de dueño se reutiliza (`validarDueno`, la misma de HU-03/HU-04) para no duplicar lógica y no tocar platos de otros restaurantes. El `404` usa `ResponseStatusException` para no cambiar el `400` que `ReglaNegocioException` devuelve en las otras HU.

## Estado actual

| HU | Estado |
|----|--------|
| HU-01 Crear propietario | ✅ Migrada a Spring Boot |
| HU-02 Crear restaurante | ✅ Migrada a Spring Boot |
| HU-03 Crear plato | ✅ Migrada a Spring Boot |
| HU-04 Modificar plato | ✅ Migrada a Spring Boot |
| HU-05 Autenticación | ✅ Migrada a Spring Boot |
| HU-07 Habilitar/Deshabilitar plato | ✅ Hecha en Spring Boot |

**Qué sigue:** resto del sprint 2 (HU-08 a HU-12). Cada HU nueva agrega su parte aquí en este README.

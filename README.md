# Gestión de Seguros — Arquitectura Hexagonal

Aplicación de escritorio en **JavaFX** para gestionar **seguros** (de vida y de vehículo) sobre **PostgreSQL**, construida con **arquitectura hexagonal (puertos y adaptadores)**.

**Autor:** Alejandra Cano
**Asignatura:** Ingeniería de Software 2 — Politécnico Grancolombiano

---

## 1. Alcance

- **CRUD solo sobre seguros:** crear, modificar, eliminar, buscar por número y listar todos.
- **Modelo de dominio** (según el diagrama de clases): `Seguro` (abstracta), `SeguroVida`, `SeguroVehiculo`, `Asegurado` y el enum `EstadoSeguro`.
- **Asegurados predeterminados:** se cargan con `sql/schema.sql`; la aplicación solo los lee para elegir el titular del seguro.
- **Patrón de diseño:** Singleton en `ConexionBD`.

### Decisiones sobre el diagrama de clases

| Diagrama | Implementación | Motivo |
|---|---|---|
| `valor: char` | `BigDecimal` | Los setters del propio diagrama ya usan `BigDecimal`; un `char` no puede guardar un valor monetario. |
| `Asegurado → Seguro` | `Seguro` guarda una referencia a su `Asegurado` | Para saber a quién pertenece cada seguro hay que guardar la referencia. |
| `Seguro` sin `id` | El **número** es la llave primaria | Se respeta el diagrama: el número identifica al seguro y no se puede modificar. |
| Estados no definidos | `ACTIVO`, `VENCIDO`, `CANCELADO` | El diagrama solo dice `EstadoSeguro`. Cámbielos en `EstadoSeguro.java` y en el `CHECK` de `schema.sql` si necesita otros. |

---

## 2. Arquitectura (según el diagrama hexagonal)

```
ProyectoSeguros/src/main/java/co/edu/poli/sw2/ProyectoSeguros/
├── dominio/
│   ├── modelo/        Seguro, SeguroVida, SeguroVehiculo, Asegurado, EstadoSeguro
│   └── excepcion/     SeguroException (base), Validacion, Duplicado, NoEncontrado, ConexionBD
├── aplicacion/
│   ├── puerto/entrada/   Crear/Buscar/Modificar/Eliminar SeguroUseCase, ListarSegurosUseCase, DatosSeguro
│   ├── puerto/salida/    SeguroRepository
│   └── servicio/         Crear/Buscar/Modificar/Eliminar SeguroServicio, ListarSegurosServicio,
│                         ValidadorSeguro, ConstructorSeguro
└── infraestructura/
    ├── ui/            ControlSeguro (adaptador de entrada JavaFX)
    ├── persistencia/  PostgresSeguroRepository (adaptador de salida), ConexionBD, CargadorEnv
    └── web/           App (wiring: construye y conecta todo)
```

- El **dominio** no depende de nada.
- La **aplicación** solo conoce el dominio y el puerto `SeguroRepository` (no sabe de JDBC ni de PostgreSQL).
- La **infraestructura** implementa el puerto de salida y consume los puertos de entrada. `App` es el único lugar donde se instancian las implementaciones concretas.
- `DatosSeguro` agrupa los campos del formulario para no pasar 9 parámetros por cada caso de uso.
- `ListarSegurosUseCase` incluye además `listarAsegurados()`, para llenar la lista desplegable del titular.

---

## 3. Base de datos

1. Crear la base de datos (una sola vez, como `postgres`):
   ```sql
   CREATE DATABASE segurosdb;
   ```
2. Conectarse a `segurosdb` y ejecutar **`sql/schema.sql`** (crea las tablas y carga 5 asegurados de ejemplo).

Esquema (herencia por tablas):

- **`asegurado`**: `identificacion` (PK), `nombre`, `correo_electronico`.
- **`seguro`** (padre): `numero` (PK), `fecha_expedicion`, `valor NUMERIC(14,2) > 0`, `estado`, `tipo` (`VIDA`/`VEHICULO`), `id_asegurado` (FK).
- **`seguro_vida`** (hija): `numero_seguro` (PK/FK, `ON DELETE CASCADE`), `nombre_beneficiario`, `parentesco`.
- **`seguro_vehiculo`** (hija): `numero_seguro` (PK/FK, `ON DELETE CASCADE`), `marca`.

Crear y modificar se hacen en una **transacción** sobre ambas tablas (sin registros huérfanos). Los errores de PostgreSQL se traducen a excepciones de dominio por su SQLState: `23505` número duplicado, `23503` asegurado inexistente, `23514`/`23502`/`22001`/`22003` dato inválido.

---

## 4. Patrón Singleton: `ConexionBD`

Clase `final`, constructor `private`, campo `instancia` `static volatile` y `getInstancia()` con *double-checked locking*. Cada `obtenerConexion()` abre una conexión nueva que quien la usa debe cerrar.

Orden de lectura de credenciales: (1) variables de entorno `DB_URL`, `DB_USER`, `DB_PASSWORD`; (2) archivo `.env`; (3) `db.properties` en el classpath. Si no hay URL se usa `jdbc:postgresql://localhost:5432/segurosdb`.

---

## 5. Cómo ejecutar

Requisitos: JDK 11 o superior, Maven y PostgreSQL con `segurosdb` creada.

1. Copie `.env.example` a `.env` y ponga sus datos (una variable por línea, sin espacios ni comillas):
   ```
   DB_URL=jdbc:postgresql://localhost:5432/segurosdb
   DB_USER=tu_usuario
   DB_PASSWORD=tu_password
   ```
2. Ejecute `sql/schema.sql` (sección 3).
3. Desde la carpeta `ProyectoSeguros`:
   ```bash
   mvn clean javafx:run
   ```
   En Eclipse: importe como *Existing Maven Project* y ejecute `infraestructura/web/App.java`.

### Uso de la interfaz

- El formulario cambia según el tipo: **VIDA** pide beneficiario y parentesco; **VEHICULO** pide marca.
- Al seleccionar una fila de la tabla se cargan sus datos y quedan **bloqueados el número y el tipo** (identifican al registro). Pulse **Limpiar** para volver al modo de creación.
- **Buscar** filtra la tabla por número; **Listar todos** la restaura.

---

## 6. Pruebas

JUnit 5 en `src/test/java`.

**Unitarias (sin base de datos)** — `mvn test`:

| Clase | Qué verifica |
|---|---|
| `ModeloDominioTest` | Entidades, setters, tipos, `equals` de `Asegurado`, enum |
| `ValidadorSeguroTest` | Número, fecha, valor (coma/punto, límites), estado, asegurado, beneficiario, parentesco, marca |
| `CrearSeguroServicioTest` | Crear vida y vehículo, recorte de espacios, duplicados y que un dato inválido **nunca llegue** al repositorio |
| `ModificarSeguroServicioTest` | Actualización, número y tipo inmutables, dato inválido no altera el original, inexistente |
| `EliminarSeguroServicioTest` | Eliminar, solo el indicado, sin selección, inexistente |
| `BuscarYListarSegurosServicioTest` | Búsqueda, inexistente, vacío, listado y asegurados |
| `ConexionBDTest` | Estructura del Singleton (misma instancia, 20 hilos, constructor privado, `final`, `static volatile`, `toString` sin credenciales) |

Los casos de uso se prueban con `RepositorioEnMemoria`, una implementación falsa del puerto `SeguroRepository`.

**Integración (requieren PostgreSQL, `schema.sql` y `.env`)** — `mvn test -Dtest=*IT`:

- `PostgresSeguroRepositoryIT`: CRUD real de ambos tipos, cascada, duplicado con *rollback*, asegurado inexistente y tipos no soportados. Usa números `TEST-xxxxxxxx` y los borra al terminar.

---

## 7. Seguridad de credenciales

`.env` y `db.properties` están en `.gitignore` y no se suben. Cada integrante crea el suyo desde `.env.example`. `ConexionBD.toString()` devuelve un texto fijo, así que las credenciales no se filtran en un `println`.

## 8. Solución de problemas

| Síntoma | Causa probable | Solución |
|---|---|---|
| `Location is not set` | `seguro.fxml` fuera de `src/main/resources` | Dejarlo en `src/main/resources/co/edu/poli/sw2/seguros/vista/` |
| `Faltan las credenciales...` | No existe `.env` o le faltan claves | Crearlo desde `.env.example` |
| `password authentication failed` | Usuario o contraseña incorrectos | Revisar el `.env` |
| `Connection ... refused` | PostgreSQL apagado | Iniciar el servicio de PostgreSQL |
| `database "segurosdb" does not exist` | No se creó la base | `CREATE DATABASE segurosdb;` y ejecutar `schema.sql` |
| `relation "seguro" does not exist` | Falta ejecutar `schema.sql` | Ejecutarlo sobre `segurosdb` |
| El combo de asegurados está vacío | Falta el `INSERT` de asegurados | Ejecutar la última sección de `schema.sql` |

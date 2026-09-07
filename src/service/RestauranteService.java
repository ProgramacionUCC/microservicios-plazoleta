package service;

import model.Propietario;
import model.Restaurante;
import repository.PropietarioRepository;
import repository.RestauranteRepository;

public class RestauranteService {

    private final RestauranteRepository restauranteRepository;
    private final PropietarioRepository propietarioRepository;
    private final AutenticacionService authService;

    public RestauranteService(RestauranteRepository restauranteRepository, PropietarioRepository propietarioRepository) {
        this.restauranteRepository = restauranteRepository;
        this.propietarioRepository = propietarioRepository;
        this.authService = new AutenticacionService(propietarioRepository);
    }

    public RestauranteService(RestauranteRepository restauranteRepository, PropietarioRepository propietarioRepository, AutenticacionService authService) {
        this.restauranteRepository = restauranteRepository;
        this.propietarioRepository = propietarioRepository;
        this.authService = authService;
    }

    public void crearRestaurante(Restaurante restaurante) {
        if (restaurante == null) throw new IllegalArgumentException("Restaurante no puede ser nulo");
        //  Validar que no vengan campos nulos o vacíos (Campos obligatorios)
        if (esNuloOVacio(restaurante.getNombre()) ||
                esNuloOVacio(restaurante.getNit()) ||
                esNuloOVacio(restaurante.getDireccion()) ||
                esNuloOVacio(restaurante.getTelefono()) ||
                esNuloOVacio(restaurante.getUrlLogo()) ||
                esNuloOVacio(restaurante.getIdPropietario())) {
            throw new IllegalArgumentException("Todos los campos (Nombre, NIT, Dirección, Teléfono, UrlLogo e idPropietario) son obligatorios.");
        }

        //  Validar que el nombre no contenga únicamente números
        if (restaurante.getNombre().trim().matches("^\\d+$")) {
            throw new IllegalArgumentException("El nombre del restaurante no puede contener únicamente números.");
        }

        //  Validar que el NIT sea únicamente numérico
        if (!restaurante.getNit().trim().matches("^\\d+$")) {
            throw new IllegalArgumentException("El NIT debe ser únicamente numérico.");
        }

        //  Validar formato de Teléfono: únicamente numérico, opcional '+' al inicio y máximo 13 caracteres (ej: +573005698325)
        String telefono = restaurante.getTelefono().trim();
        if (telefono.length() > 13 || !telefono.matches("^\\+?\\d+$")) {
            throw new IllegalArgumentException("El teléfono debe contener únicamente números, máximo 13 caracteres en total y puede incluir '+' al inicio.");
        }

        //  Validar que el idPropietario corresponda a un usuario existente con rol PROPIETARIO
        boolean existePropietario = propietarioRepository.getPropietarios().stream()
                .anyMatch(p -> p.getDocumentoDeIdentidad().equals(restaurante.getIdPropietario()) && "PROPIETARIO".equals(p.getRol()));
        if (!existePropietario) {
            throw new IllegalArgumentException("El ID del propietario no corresponde a un usuario registrado con dicho rol.");
        }

        // UNIQUE: NIT y nombre no se repiten
        if (restauranteRepository.obtenerTodos().stream().anyMatch(r -> r.getNit().equals(restaurante.getNit()))) {
            throw new IllegalArgumentException("NIT ya registrado (UNIQUE)");
        }
        if (restauranteRepository.obtenerTodos().stream().anyMatch(r -> r.getNombre().equalsIgnoreCase(restaurante.getNombre()))) {
            throw new IllegalArgumentException("Nombre restaurante ya existe (UNIQUE)");
        }

        // CHECK: ya validado arriba (nombre no solo numeros, NIT numerico, telefono formato)

        // Guardar restaurante en memoria tras pasar todas las validaciones
        restauranteRepository.guardar(restaurante);
    }

    // Metodo con autenticacion HU-05: solo ADMINISTRADOR puede crear restaurantes
    public void crearRestaurante(Restaurante restaurante, Propietario usuarioAutenticado) {
        if (usuarioAutenticado == null) {
            throw new IllegalArgumentException("Debe estar autenticado para crear restaurante.");
        }
        if (!authService.tienePermiso(usuarioAutenticado, "ADMINISTRADOR")) {
            throw new IllegalArgumentException("Solo el administrador puede crear restaurantes.");
        }
        crearRestaurante(restaurante);
    }

    private boolean esNuloOVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}

package com.plazoleta.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Respuesta para las listas paginadas (HU-10; tambien sirve para HU-09 y HU-12).
 *
 * Paginar = no devolver todo de una vez, sino por "paginas" de un tamaño escogido.
 * Ej: 12 platos con tamano 5 -> pagina 0 (platos 1-5), pagina 1 (6-10), pagina 2 (11-12).
 *
 * <T> significa que sirve para cualquier tipo de dato:
 * PaginaResponseDTO<PlatoResponseDTO>, PaginaResponseDTO<RestauranteResponseDTO>, etc.
 */
@Getter
@Setter
@Builder
public class PaginaResponseDTO<T> {
    private List<T> contenido;      // Los elementos de esta pagina
    private int pagina;             // Numero de la pagina actual (empieza en 0)
    private int tamano;             // Cuantos elementos por pagina se pidieron
    private long totalElementos;    // Cuantos elementos hay en total
    private int totalPaginas;       // Cuantas paginas hay en total
}

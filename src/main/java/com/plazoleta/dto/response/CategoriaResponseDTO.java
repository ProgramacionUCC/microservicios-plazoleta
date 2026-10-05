package com.plazoleta.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Lo que se devuelve al crear o listar categorias.
 * El id sirve para luego crear platos con esa categoria.
 */
@Getter
@Setter
@Builder
public class CategoriaResponseDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
}

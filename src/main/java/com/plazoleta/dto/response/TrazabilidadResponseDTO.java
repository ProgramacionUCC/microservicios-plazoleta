package com.plazoleta.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Una fila del historial de un pedido (HU-17).
 * Ej: el pedido paso a EN_PREPARACION el 2026-11-15 a las 10:05.
 */
@Getter
@Setter
@Builder
public class TrazabilidadResponseDTO {
    private String estado;          // PENDIENTE, EN_PREPARACION, LISTO, ENTREGADO o CANCELADO
    private LocalDateTime fecha;    // Momento exacto del cambio de estado
}
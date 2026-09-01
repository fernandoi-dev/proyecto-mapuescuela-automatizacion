package com.mapuescuela.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DetallePedidoDto {

    private Long productoId;
    private String nombre;
    private Integer cantidad;
    private Integer precio;
}

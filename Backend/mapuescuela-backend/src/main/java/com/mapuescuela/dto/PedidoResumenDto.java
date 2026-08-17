package com.mapuescuela.dto;

import com.mapuescuela.model.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PedidoResumenDto {

    private Long id;
    private String cliente;
    private Integer total;
    private EstadoPedido estado;
}

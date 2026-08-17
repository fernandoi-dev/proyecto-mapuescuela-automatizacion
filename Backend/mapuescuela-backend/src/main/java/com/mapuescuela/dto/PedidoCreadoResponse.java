package com.mapuescuela.dto;

import com.mapuescuela.model.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PedidoCreadoResponse {

    private Long id;
    private EstadoPedido estado;
    private Integer total;
    private String processInstanceId;
}

package com.mapuescuela.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearPedidoRequest {

    @NotNull
    private Long clienteId;

    @NotNull
    private String modalidadEntrega;

    @NotEmpty
    @Valid
    private List<ItemPedidoRequest> productos;
}

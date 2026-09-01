package com.mapuescuela.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductoRequest {

    @NotBlank
    private String nombre;

    private String descripcion;
    private String categoria;

    @NotNull
    @Min(0)
    private Integer precio;

    private String fotografia;

    @NotNull
    @Min(0)
    private Integer stock;

    @NotBlank
    private String estado;
}

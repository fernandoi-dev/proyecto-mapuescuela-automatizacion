package com.mapuescuela.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteRequest {

    @NotBlank
    private String nombre;

    @NotBlank
    @Email
    private String correo;

    private String telefono;
    private String direccion;
}

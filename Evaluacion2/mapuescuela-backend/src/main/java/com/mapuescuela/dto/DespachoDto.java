package com.mapuescuela.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DespachoDto {

    private String empresaTransporte;
    private String numeroSeguimiento;
    private LocalDateTime fechaEnvio;
}

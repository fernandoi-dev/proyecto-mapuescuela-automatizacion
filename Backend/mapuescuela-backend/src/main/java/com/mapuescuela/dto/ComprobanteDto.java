package com.mapuescuela.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ComprobanteDto {

    private Long id;
    private String archivo;
    private LocalDateTime fechaSubida;
    private String estado;
}

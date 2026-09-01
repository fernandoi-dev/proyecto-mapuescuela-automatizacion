package com.mapuescuela.dto;

import com.mapuescuela.model.EstadoPedido;
import com.mapuescuela.model.ModalidadEntrega;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PedidoDetalleDto {

    private Long id;
    private String cliente;
    private String correo;
    private String telefono;
    private String direccion;
    private LocalDateTime fecha;
    private Integer total;
    private ModalidadEntrega modalidadEntrega;
    private EstadoPedido estado;
    private String processInstanceId;
    private List<DetallePedidoDto> productos;
    private ComprobanteDto comprobante;
    private DespachoDto despacho;
}

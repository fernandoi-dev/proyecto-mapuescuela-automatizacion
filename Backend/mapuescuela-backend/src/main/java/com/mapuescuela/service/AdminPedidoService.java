package com.mapuescuela.service;

import com.mapuescuela.dto.ComprobanteDto;
import com.mapuescuela.dto.DespachoDto;
import com.mapuescuela.dto.DetallePedidoDto;
import com.mapuescuela.dto.PedidoDetalleDto;
import com.mapuescuela.dto.PedidoResumenDto;
import com.mapuescuela.dto.PrepararPedidoRequest;
import com.mapuescuela.exception.RecursoNoEncontradoException;
import com.mapuescuela.exception.ReglaNegocioException;
import com.mapuescuela.model.Comprobante;
import com.mapuescuela.model.Despacho;
import com.mapuescuela.model.EstadoPedido;
import com.mapuescuela.model.ModalidadEntrega;
import com.mapuescuela.model.Pedido;
import com.mapuescuela.repository.DespachoRepository;
import com.mapuescuela.repository.PedidoRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminPedidoService {

    private final PedidoRepository pedidoRepository;
    private final DespachoRepository despachoRepository;
    private final FlowableProcessService flowableProcessService;
    private final ArchivoStorageService archivoStorageService;

    public List<PedidoResumenDto> listar() {
        return pedidoRepository.findAllConCliente().stream()
                .map(pedido -> new PedidoResumenDto(
                        pedido.getId(),
                        pedido.getCliente().getNombre(),
                        pedido.getTotal(),
                        pedido.getEstado()
                ))
                .toList();
    }

    public PedidoDetalleDto obtener(Long id) {
        Pedido pedido = pedidoRepository.findDetalleById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado: " + id));
        return mapearDetalle(pedido);
    }

    @Transactional
    public PedidoDetalleDto aprobarPago(Long id) {
        Pedido pedido = pedidoRepository.findDetalleById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado: " + id));

        if (pedido.getEstado() != EstadoPedido.PAGO_EN_REVISION) {
            throw new ReglaNegocioException("Solo se puede aprobar un pedido en PAGO_EN_REVISION");
        }

        if (pedido.getComprobante() != null) {
            pedido.getComprobante().setEstado("APROBADO");
        }

        pedido.setEstado(EstadoPedido.PAGO_APROBADO);
        pedidoRepository.saveAndFlush(pedido);
        flowableProcessService.completarRevisionPago(pedido.getProcessInstanceId(), true);

        return obtener(id);
    }

    @Transactional
    public PedidoDetalleDto rechazarPago(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado: " + id));

        if (pedido.getEstado() != EstadoPedido.PAGO_EN_REVISION) {
            throw new ReglaNegocioException("Solo se puede rechazar un pedido en PAGO_EN_REVISION");
        }

        if (pedido.getComprobante() != null) {
            pedido.getComprobante().setEstado("RECHAZADO");
        }

        pedido.setEstado(EstadoPedido.PAGO_RECHAZADO);
        pedidoRepository.saveAndFlush(pedido);
        flowableProcessService.completarRevisionPago(pedido.getProcessInstanceId(), false);

        return obtener(id);
    }

    @Transactional
    public PedidoDetalleDto preparar(Long id, PrepararPedidoRequest request) {
        Pedido pedido = pedidoRepository.findDetalleById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado: " + id));

        if (pedido.getEstado() != EstadoPedido.EN_PREPARACION) {
            throw new ReglaNegocioException("El pedido debe estar EN_PREPARACION para gestionar la entrega");
        }

        if (pedido.getModalidadEntrega() == ModalidadEntrega.RETIRO) {
            pedido.setEstado(EstadoPedido.LISTO_PARA_RETIRO);
        } else {
            if (request == null
                    || request.getEmpresaTransporte() == null || request.getEmpresaTransporte().isBlank()
                    || request.getNumeroSeguimiento() == null || request.getNumeroSeguimiento().isBlank()) {
                throw new ReglaNegocioException("Para despacho debe indicar empresaTransporte y numeroSeguimiento");
            }

            Despacho despacho = pedido.getDespacho();
            if (despacho == null) {
                despacho = new Despacho();
                despacho.setPedido(pedido);
                pedido.setDespacho(despacho);
            }
            despacho.setEmpresaTransporte(request.getEmpresaTransporte());
            despacho.setNumeroSeguimiento(request.getNumeroSeguimiento());
            despacho.setFechaEnvio(LocalDateTime.now());
            despachoRepository.save(despacho);
            pedido.setEstado(EstadoPedido.ENVIADO);
        }

        pedidoRepository.save(pedido);
        flowableProcessService.completarEntrega(pedido.getProcessInstanceId(), pedido.getModalidadEntrega());
        return obtener(id);
    }

    @Transactional
    public PedidoDetalleDto finalizar(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado: " + id));

        if (pedido.getEstado() != EstadoPedido.LISTO_PARA_RETIRO
                && pedido.getEstado() != EstadoPedido.ENVIADO) {
            throw new ReglaNegocioException("El pedido aún no está listo para finalizar");
        }

        pedido.setEstado(EstadoPedido.FINALIZADO);
        pedidoRepository.save(pedido);
        return obtener(id);
    }

    public java.nio.file.Path archivoComprobante(Long id) {
        Pedido pedido = pedidoRepository.findDetalleById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado: " + id));
        if (pedido.getComprobante() == null) {
            throw new RecursoNoEncontradoException("El pedido no tiene comprobante");
        }
        return archivoStorageService.resolver(pedido.getComprobante().getArchivo());
    }

    private PedidoDetalleDto mapearDetalle(Pedido pedido) {
        Comprobante comprobante = pedido.getComprobante();
        Despacho despacho = pedido.getDespacho();

        return PedidoDetalleDto.builder()
                .id(pedido.getId())
                .cliente(pedido.getCliente().getNombre())
                .correo(pedido.getCliente().getCorreo())
                .telefono(pedido.getCliente().getTelefono())
                .direccion(pedido.getCliente().getDireccion())
                .fecha(pedido.getFecha())
                .total(pedido.getTotal())
                .modalidadEntrega(pedido.getModalidadEntrega())
                .estado(pedido.getEstado())
                .processInstanceId(pedido.getProcessInstanceId())
                .productos(pedido.getDetalles().stream()
                        .map(detalle -> new DetallePedidoDto(
                                detalle.getProducto().getId(),
                                detalle.getProducto().getNombre(),
                                detalle.getCantidad(),
                                detalle.getPrecio()
                        ))
                        .toList())
                .comprobante(comprobante == null ? null : new ComprobanteDto(
                        comprobante.getId(),
                        comprobante.getArchivo(),
                        comprobante.getFechaSubida(),
                        comprobante.getEstado()
                ))
                .despacho(despacho == null ? null : new DespachoDto(
                        despacho.getEmpresaTransporte(),
                        despacho.getNumeroSeguimiento(),
                        despacho.getFechaEnvio()
                ))
                .build();
    }
}

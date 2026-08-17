package com.mapuescuela.service;

import com.mapuescuela.dto.ComprobanteDto;
import com.mapuescuela.dto.CrearPedidoRequest;
import com.mapuescuela.dto.ItemPedidoRequest;
import com.mapuescuela.dto.PedidoCreadoResponse;
import com.mapuescuela.exception.RecursoNoEncontradoException;
import com.mapuescuela.exception.ReglaNegocioException;
import com.mapuescuela.model.Cliente;
import com.mapuescuela.model.Comprobante;
import com.mapuescuela.model.DetallePedido;
import com.mapuescuela.model.EstadoPedido;
import com.mapuescuela.model.ModalidadEntrega;
import com.mapuescuela.model.Pedido;
import com.mapuescuela.model.Producto;
import com.mapuescuela.repository.ClienteRepository;
import com.mapuescuela.repository.ComprobanteRepository;
import com.mapuescuela.repository.PedidoRepository;
import com.mapuescuela.repository.ProductoRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final ComprobanteRepository comprobanteRepository;
    private final FlowableProcessService flowableProcessService;
    private final ArchivoStorageService archivoStorageService;

    @Transactional
    public PedidoCreadoResponse crear(CrearPedidoRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado: " + request.getClienteId()));

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFecha(LocalDateTime.now());
        pedido.setModalidadEntrega(parseModalidad(request.getModalidadEntrega()));
        pedido.setEstado(EstadoPedido.PENDIENTE_PAGO);

        int total = 0;
        for (ItemPedidoRequest item : request.getProductos()) {
            Producto producto = productoRepository.findById(item.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto no encontrado: " + item.getProductoId()));

            if (!"Disponible".equalsIgnoreCase(producto.getEstado())) {
                throw new ReglaNegocioException("El producto '" + producto.getNombre() + "' no está disponible");
            }
            if (producto.getStock() == null || producto.getStock() < item.getCantidad()) {
                throw new ReglaNegocioException("Stock insuficiente para '" + producto.getNombre() + "'");
            }

            DetallePedido detalle = new DetallePedido();
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecio(producto.getPrecio());
            pedido.agregarDetalle(detalle);
            total += producto.getPrecio() * item.getCantidad();
        }

        pedido.setTotal(total);
        pedido = pedidoRepository.saveAndFlush(pedido);

        String processInstanceId = flowableProcessService.iniciarProcesoVenta(pedido.getId());
        pedido.setProcessInstanceId(processInstanceId);
        pedidoRepository.save(pedido);

        return new PedidoCreadoResponse(pedido.getId(), pedido.getEstado(), pedido.getTotal(), processInstanceId);
    }

    @Transactional
    public ComprobanteDto subirComprobante(Long pedidoId, MultipartFile archivo) {
        Pedido pedido = pedidoRepository.findDetalleById(pedidoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Pedido no encontrado: " + pedidoId));

        if (pedido.getEstado() != EstadoPedido.PENDIENTE_PAGO
                && pedido.getEstado() != EstadoPedido.PAGO_EN_REVISION) {
            throw new ReglaNegocioException("El pedido no admite comprobante en estado " + pedido.getEstado());
        }

        String nombreArchivo = archivoStorageService.guardar(pedidoId, archivo);

        Comprobante comprobante = pedido.getComprobante();
        boolean esPrimeraCarga = comprobante == null;
        if (comprobante == null) {
            comprobante = new Comprobante();
            comprobante.setPedido(pedido);
            pedido.setComprobante(comprobante);
        }

        comprobante.setArchivo(nombreArchivo);
        comprobante.setFechaSubida(LocalDateTime.now());
        comprobante.setEstado("SUBIDO");
        comprobanteRepository.save(comprobante);

        pedido.setEstado(EstadoPedido.PAGO_EN_REVISION);
        pedidoRepository.saveAndFlush(pedido);

        if (esPrimeraCarga) {
            flowableProcessService.notificarComprobanteRecibido(pedido.getProcessInstanceId());
        }

        return new ComprobanteDto(
                comprobante.getId(),
                comprobante.getArchivo(),
                comprobante.getFechaSubida(),
                comprobante.getEstado()
        );
    }

    private ModalidadEntrega parseModalidad(String valor) {
        if (valor == null) {
            throw new ReglaNegocioException("Debe indicar modalidadEntrega");
        }
        String normalizado = valor.trim().toUpperCase();
        if ("COURIER".equals(normalizado)) {
            return ModalidadEntrega.DESPACHO;
        }
        try {
            return ModalidadEntrega.valueOf(normalizado);
        } catch (IllegalArgumentException ex) {
            throw new ReglaNegocioException("Modalidad de entrega inválida. Use RETIRO o DESPACHO");
        }
    }
}

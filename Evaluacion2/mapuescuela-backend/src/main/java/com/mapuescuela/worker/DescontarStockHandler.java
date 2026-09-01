package com.mapuescuela.worker;

import com.mapuescuela.model.EstadoPedido;
import com.mapuescuela.model.Pedido;
import com.mapuescuela.model.Producto;
import com.mapuescuela.repository.PedidoRepository;
import com.mapuescuela.repository.ProductoRepository;
import java.util.EnumSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.job.api.AcquiredExternalWorkerJob;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DescontarStockHandler implements ExternalTopicHandler {

    private static final Set<EstadoPedido> YA_DESCONTADO = EnumSet.of(
            EstadoPedido.EN_PREPARACION,
            EstadoPedido.LISTO_PARA_RETIRO,
            EstadoPedido.ENVIADO,
            EstadoPedido.FINALIZADO
    );

    private final PedidoIdResolver pedidoIdResolver;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    @Override
    public String topic() {
        return "descontar-stock";
    }

    @Override
    @Transactional
    public void handle(AcquiredExternalWorkerJob job) {
        Long pedidoId = pedidoIdResolver.resolve(job);
        Pedido pedido = pedidoRepository.findDetalleById(pedidoId)
                .orElseThrow(() -> new IllegalStateException("Pedido no encontrado: " + pedidoId));

        if (YA_DESCONTADO.contains(pedido.getEstado())) {
            log.info("Topic descontar-stock: pedido {} ya descontó stock (estado={})", pedidoId, pedido.getEstado());
            return;
        }

        for (var detalle : pedido.getDetalles()) {
            Producto producto = detalle.getProducto();
            if (producto.getStock() == null || producto.getStock() < detalle.getCantidad()) {
                throw new IllegalStateException(
                        "Stock insuficiente al descontar '" + producto.getNombre() + "' del pedido " + pedidoId);
            }
            producto.setStock(producto.getStock() - detalle.getCantidad());
            if (producto.getStock() == 0) {
                producto.setEstado("Retirado");
            }
            productoRepository.save(producto);
        }

        pedido.setEstado(EstadoPedido.EN_PREPARACION);
        pedidoRepository.save(pedido);
        log.info("Topic descontar-stock: pedido {} descontó stock y pasó a EN_PREPARACION", pedidoId);
    }
}

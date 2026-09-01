package com.mapuescuela.worker;

import com.mapuescuela.model.EstadoPedido;
import com.mapuescuela.model.Pedido;
import com.mapuescuela.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.job.api.AcquiredExternalWorkerJob;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrarRechazoHandler implements ExternalTopicHandler {

    private final PedidoIdResolver pedidoIdResolver;
    private final PedidoRepository pedidoRepository;

    @Override
    public String topic() {
        return "registrar-rechazo";
    }

    @Override
    @Transactional
    public void handle(AcquiredExternalWorkerJob job) {
        Long pedidoId = pedidoIdResolver.resolve(job);
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalStateException("Pedido no encontrado: " + pedidoId));

        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            log.info("Topic registrar-rechazo: pedido {} ya estaba CANCELADO", pedidoId);
            return;
        }

        if (pedido.getEstado() != EstadoPedido.PAGO_RECHAZADO) {
            pedido.setEstado(EstadoPedido.PAGO_RECHAZADO);
            pedidoRepository.saveAndFlush(pedido);
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        pedidoRepository.save(pedido);
        log.info("Topic registrar-rechazo: pedido {} pasó a PAGO_RECHAZADO y luego CANCELADO", pedidoId);
    }
}

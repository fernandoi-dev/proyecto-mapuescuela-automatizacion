package com.mapuescuela.worker;

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
public class GenerarPedidoHandler implements ExternalTopicHandler {

    private final PedidoIdResolver pedidoIdResolver;
    private final PedidoRepository pedidoRepository;

    @Override
    public String topic() {
        return "generar-pedido";
    }

    @Override
    @Transactional
    public void handle(AcquiredExternalWorkerJob job) {
        Long pedidoId = pedidoIdResolver.resolve(job);
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalStateException("Pedido no encontrado: " + pedidoId));

        log.info("Topic generar-pedido completado. pedidoId={}, estado={}, processInstanceId={}",
                pedido.getId(), pedido.getEstado(), job.getProcessInstanceId());
    }
}

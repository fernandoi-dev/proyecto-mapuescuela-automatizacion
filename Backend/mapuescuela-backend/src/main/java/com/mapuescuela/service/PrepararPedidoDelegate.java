package com.mapuescuela.service;

import com.mapuescuela.model.EstadoPedido;
import com.mapuescuela.model.Pedido;
import com.mapuescuela.repository.PedidoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("prepararPedidoDelegate")
@RequiredArgsConstructor
public class PrepararPedidoDelegate implements JavaDelegate {

    private final PedidoRepository pedidoRepository;

    @Override
    public void execute(DelegateExecution execution) {
        Long pedidoId = toLong(execution.getVariable("pedidoId"));
        Pedido pedido = pedidoRepository.findById(pedidoId).orElse(null);
        if (pedido == null) {
            log.warn("No se encontró el pedido {} para preparar", pedidoId);
            return;
        }

        pedido.setEstado(EstadoPedido.EN_PREPARACION);
        pedidoRepository.save(pedido);
        log.info("Pedido {} pasó a EN_PREPARACION", pedidoId);
    }

    private Long toLong(Object valor) {
        if (valor instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(String.valueOf(valor));
    }
}

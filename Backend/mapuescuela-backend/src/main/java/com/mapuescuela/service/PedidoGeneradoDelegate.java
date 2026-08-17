package com.mapuescuela.service;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Slf4j
@Component("pedidoGeneradoDelegate")
public class PedidoGeneradoDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        log.info("Pedido generado. pedidoId={}, processInstanceId={}",
                execution.getVariable("pedidoId"),
                execution.getProcessInstanceId());
    }
}

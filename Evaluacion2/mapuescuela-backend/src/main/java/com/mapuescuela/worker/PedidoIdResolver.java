package com.mapuescuela.worker;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.job.api.AcquiredExternalWorkerJob;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PedidoIdResolver {

    private final RuntimeService runtimeService;

    public Long resolve(AcquiredExternalWorkerJob job) {
        Object valor = variable(job, "pedidoId");
        if (valor == null) {
            throw new IllegalStateException("El job " + job.getId() + " no trae la variable pedidoId");
        }
        return toLong(valor);
    }

    public Object variable(AcquiredExternalWorkerJob job, String nombre) {
        Map<String, Object> variables = job.getVariables();
        Object valor = variables == null ? null : variables.get(nombre);
        if (valor == null && job.getProcessInstanceId() != null) {
            valor = runtimeService.getVariable(job.getProcessInstanceId(), nombre);
        }
        return valor;
    }

    public Long toLong(Object valor) {
        if (valor instanceof Number number) {
            return number.longValue();
        }
        return Long.valueOf(String.valueOf(valor));
    }
}

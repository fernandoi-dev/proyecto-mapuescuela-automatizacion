package com.mapuescuela.service;

import com.mapuescuela.exception.ReglaNegocioException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FlowableProcessService {

    public static final String PROCESO_VENTA = "procesoVenta";
    public static final String TAREA_ESPERAR_COMPROBANTE = "esperarComprobante";
    public static final String TAREA_REVISAR_COMPROBANTE = "revisarComprobante";

    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public String iniciarProcesoVenta(Long pedidoId) {
        ProcessInstance proceso = runtimeService.startProcessInstanceByKey(
                PROCESO_VENTA,
                pedidoId.toString(),
                Map.of("pedidoId", pedidoId)
        );
        return proceso.getId();
    }

    public void notificarComprobanteRecibido(String processInstanceId) {
        completarTarea(processInstanceId, TAREA_ESPERAR_COMPROBANTE, null);
    }

    public void completarRevisionPago(String processInstanceId, boolean aprobado) {
        completarTarea(processInstanceId, TAREA_REVISAR_COMPROBANTE, Map.of("pagoAprobado", aprobado));
    }

    private void completarTarea(String processInstanceId, String taskKey, Map<String, Object> variables) {
        if (processInstanceId == null || processInstanceId.isBlank()) {
            throw new ReglaNegocioException("El pedido no tiene un proceso Flowable asociado");
        }

        Task tarea = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .taskDefinitionKey(taskKey)
                .singleResult();

        if (tarea == null) {
            throw new ReglaNegocioException("No hay una tarea activa '" + taskKey + "' para este pedido");
        }

        if (variables == null) {
            taskService.complete(tarea.getId());
        } else {
            taskService.complete(tarea.getId(), variables);
        }
    }
}

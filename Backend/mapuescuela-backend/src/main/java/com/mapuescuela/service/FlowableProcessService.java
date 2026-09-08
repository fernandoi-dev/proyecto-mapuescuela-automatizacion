package com.mapuescuela.service;

import com.mapuescuela.exception.ReglaNegocioException;
import com.mapuescuela.model.ModalidadEntrega;
import java.util.HashMap;
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

    public static final String PROCESO_VENTA = "procesoVentaMapuescuela.p";
    public static final String TAREA_ADJUNTAR_COMPROBANTE = "FormTask_11";
    public static final String TAREA_REVISAR_COMPROBANTE = "FormTask_18";
    public static final String TAREA_ENTREGAR_SEDE = "FormTask_29";
    public static final String TAREA_COORDINAR_DESPACHO = "FormTask_33";

    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public String iniciarProcesoVenta(Long pedidoId, ModalidadEntrega modalidad) {
        String tipoEntrega = modalidad == ModalidadEntrega.RETIRO ? "retiro" : "despacho";
        Map<String, Object> variables = new HashMap<>();
        variables.put("pedidoId", pedidoId);
        variables.put("tipoEntrega", tipoEntrega);
        variables.put("initiator", "mapuescuela-backend");

        ProcessInstance proceso = runtimeService.startProcessInstanceByKey(
                PROCESO_VENTA,
                pedidoId.toString(),
                variables
        );
        return proceso.getId();
    }

    public void notificarComprobanteRecibido(String processInstanceId) {
        completarTareaConReintento(processInstanceId, TAREA_ADJUNTAR_COMPROBANTE, null, 10, 500);
    }

    public void completarRevisionPago(String processInstanceId, boolean aprobado) {
        completarTarea(processInstanceId, TAREA_REVISAR_COMPROBANTE, Map.of("pagoAprobado", aprobado));
    }

    public void completarEntrega(String processInstanceId, ModalidadEntrega modalidad) {
        String taskKey = modalidad == ModalidadEntrega.RETIRO
                ? TAREA_ENTREGAR_SEDE
                : TAREA_COORDINAR_DESPACHO;
        completarTarea(processInstanceId, taskKey, null);
    }

    private void completarTareaConReintento(
            String processInstanceId,
            String taskKey,
            Map<String, Object> variables,
            int intentos,
            long esperaMs
    ) {
        ReglaNegocioException ultimo = null;
        for (int i = 0; i < intentos; i++) {
            try {
                completarTarea(processInstanceId, taskKey, variables);
                return;
            } catch (ReglaNegocioException ex) {
                ultimo = ex;
                try {
                    Thread.sleep(esperaMs);
                } catch (InterruptedException interrupted) {
                    Thread.currentThread().interrupt();
                    throw ex;
                }
            }
        }
        throw ultimo;
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

package com.mapuescuela.worker;

import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.ManagementService;
import org.flowable.job.api.AcquiredExternalWorkerJob;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalWorkerPoller {

    static final String WORKER_ID = "mapuescuela-backend";

    private final ManagementService managementService;
    private final List<ExternalTopicHandler> handlers;

    @Scheduled(fixedDelay = 2000)
    public void poll() {
        for (ExternalTopicHandler handler : handlers) {
            pollTopic(handler);
        }
    }

    private void pollTopic(ExternalTopicHandler handler) {
        List<AcquiredExternalWorkerJob> jobs = managementService
                .createExternalWorkerJobAcquireBuilder()
                .topic(handler.topic(), Duration.ofSeconds(30))
                .acquireAndLock(1, WORKER_ID);

        for (AcquiredExternalWorkerJob job : jobs) {
            try {
                handler.handle(job);
                managementService.createExternalWorkerCompletionBuilder(job.getId(), WORKER_ID)
                        .complete();
            } catch (Exception ex) {
                log.error("Fallo el worker topic={} job={} pedidoVar={}",
                        handler.topic(), job.getId(), job.getVariables(), ex);
                String mensaje = ex.getMessage() == null ? ex.getClass().getSimpleName() : ex.getMessage();
                if (mensaje.length() > 1800) {
                    mensaje = mensaje.substring(0, 1800);
                }
                managementService.createExternalWorkerJobFailureBuilder(job.getId(), WORKER_ID)
                        .errorMessage(mensaje)
                        .fail();
            }
        }
    }
}

package com.mapuescuela.worker;

import org.flowable.job.api.AcquiredExternalWorkerJob;

public interface ExternalTopicHandler {

    String topic();

    void handle(AcquiredExternalWorkerJob job);
}

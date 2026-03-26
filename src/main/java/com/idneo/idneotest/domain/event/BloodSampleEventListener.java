package com.idneo.idneotest.domain.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BloodSampleEventListener {

    @EventListener
    public void onBloodSampleProcessed(BloodSampleProcessedEvent event) {
        // Some external service can be triggered here
        log.info("Blood sample processed: id={}, patientId={}, processedAt={}",
                event.sampleId(), event.patientId(), event.processedAt());
    }
}

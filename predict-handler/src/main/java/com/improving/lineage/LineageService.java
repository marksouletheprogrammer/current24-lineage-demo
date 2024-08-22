package com.improving.lineage;

import io.openlineage.client.OpenLineage;
import io.openlineage.client.OpenLineageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.UUID;

@Service
public class LineageService {

    @Autowired
    private OpenLineage openLineage;

    @Autowired
    private OpenLineageClient openLineageClient;

    public void reportLineage() {
        // Create job information
        OpenLineage.Job job = openLineage.newJobBuilder()
                .namespace("animals")
                .name("dino_job2")
                .build();

        // Create job run information
        OpenLineage.Run run = openLineage.newRunBuilder()
                .runId(UUID.fromString("c9b9aa64-9b03-46d0-8683-7789d77d5ea2"))
                .build();

        // Create input dataset information
        OpenLineage.InputDataset inputDataset = openLineage.newInputDatasetBuilder()
                .namespace("animals")
                .name("hamster_dataset")
                .build();

        // Create output dataset information
        OpenLineage.OutputDataset outputDataset = openLineage.newOutputDatasetBuilder()
                .namespace("animals")
                .name("dino_dataset")
                .build();

        // Create event
        OpenLineage.RunEvent runEvent = openLineage.newRunEventBuilder()
                .eventType(OpenLineage.RunEvent.EventType.COMPLETE)
                .eventTime(ZonedDateTime.now())
                .run(run)
                .job(job)
                .inputs(Collections.singletonList(inputDataset))
                .outputs(Collections.singletonList(outputDataset))
                .build();

        // Send event to OpenLineage server
        openLineageClient.emit(runEvent);
        System.out.println("Lineage event reported");
    }
}

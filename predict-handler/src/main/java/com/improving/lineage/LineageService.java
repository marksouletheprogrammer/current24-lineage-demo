package com.improving.lineage;

import io.openlineage.client.OpenLineage;
import io.openlineage.client.OpenLineageClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
public class LineageService {

    @Autowired
    private OpenLineage openLineage;

    @Autowired
    private OpenLineageClient openLineageClient;

    private static final String NAMESPACE = "weather-prediction";
    private final static String TRACE_ID = "traceId";
    private final static String PARTITION = "partition";
    private final static String OFFSET = "offset";

    public void reportLineage(UUID traceId, int partition, long offset) {
        // Create job information.
        OpenLineage.Job job = openLineage.newJobBuilder()
                .namespace(NAMESPACE)
                .name("predict-handler")
                .facets(openLineage.newJobFacetsBuilder()
                        .jobType(openLineage.newJobTypeJobFacetBuilder()
                                .integration("KAFKA")
                                .processingType("STREAMING")
                                .jobType("EVENT")
                                .build())
                        .build())
                .build();

        // Create job run information.
        var runFacet = openLineage.newRunFacet();
        runFacet.getAdditionalProperties().put(TRACE_ID, traceId);
        runFacet.getAdditionalProperties().put(PARTITION, partition);
        runFacet.getAdditionalProperties().put(OFFSET, offset);
        OpenLineage.Run run = openLineage.newRunBuilder()
                .runId(UUID.randomUUID())
                .facets(openLineage.newRunFacetsBuilder()
                        .nominalTime(openLineage.newNominalTimeRunFacet(ZonedDateTime.now(), null))
                        .put("event", runFacet)
                        .build())
                .build();

        var inputFields = List.of(
                openLineage.newSchemaDatasetFacetFields("city", "String", null, null),
                openLineage.newSchemaDatasetFacetFields("state", "String", null, null),
                openLineage.newSchemaDatasetFacetFields("currentTemp", "int", null, null),
                openLineage.newSchemaDatasetFacetFields("predictLow", "int", null, null),
                openLineage.newSchemaDatasetFacetFields("predictHigh", "int", null, null)
        );
        // Create input dataset information.
        OpenLineage.InputDataset inputDataset = openLineage.newInputDatasetBuilder()
                .namespace(NAMESPACE)
                .name("com.weather.enriched")
                .facets(openLineage.newDatasetFacetsBuilder()
                        .schema(openLineage.newSchemaDatasetFacetBuilder()
                                .fields(inputFields)
                                .build())
                        .build())
                .build();

        var outputFields = List.of(
                openLineage.newSchemaDatasetFacetFields("id", "int", null, null),
                openLineage.newSchemaDatasetFacetFields("city", "String", null, null),
                openLineage.newSchemaDatasetFacetFields("state", "String", null, null),
                openLineage.newSchemaDatasetFacetFields("currentTemp", "int", null, null),
                openLineage.newSchemaDatasetFacetFields("predictLow", "int", null, null),
                openLineage.newSchemaDatasetFacetFields("predictHigh", "int", null, null)
        );
        // Create output dataset information.
        OpenLineage.OutputDataset outputDataset = openLineage.newOutputDatasetBuilder()
                .namespace(NAMESPACE)
                .name("weather_prediction")
                .facets(openLineage.newDatasetFacetsBuilder()
                        .schema(openLineage.newSchemaDatasetFacetBuilder()
                                .fields(outputFields)
                                .build())
                        .columnLineage(openLineage.newColumnLineageDatasetFacetBuilder()
                                .fields(openLineage.newColumnLineageDatasetFacetFieldsBuilder()
                                        .put("id", openLineage.newColumnLineageDatasetFacetFieldsAdditionalBuilder()
                                                .transformationType("INDIRECT")
                                                .transformationDescription("Generated on insert.")
                                                .inputFields(List.of())
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();

        // Create event.
        OpenLineage.RunEvent runEvent = openLineage.newRunEventBuilder()
                .eventType(OpenLineage.RunEvent.EventType.COMPLETE)
                .eventTime(ZonedDateTime.now())
                .run(run)
                .job(job)
                .inputs(Collections.singletonList(inputDataset))
                .outputs(Collections.singletonList(outputDataset))
                .build();

        // Send event to OpenLineage server.
        openLineageClient.emit(runEvent);
        System.out.println("Lineage event reported " + traceId.toString());
    }
}

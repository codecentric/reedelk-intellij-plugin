package de.codecentric.reedelk.plugin.component.type.flowreference;

import de.codecentric.reedelk.plugin.fixture.Json;
import de.codecentric.reedelk.plugin.AbstractGraphTest;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class FlowReferenceSerializerTest extends AbstractGraphTest {

    private FlowReferenceSerializer serializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        serializer = new FlowReferenceSerializer();
    }

    @Test
    void shouldCorrectlySerializeFlowReference() {
        // Given
        JSONArray sequence = new JSONArray();

        String expectedReference = "11a2ce60-5c9d-1111-82a7-f1fa1111f333";

        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, flowReferenceNode1);

        flowReferenceNode1.componentData().set("ref", expectedReference);

        // When
        serializer.serialize(graph, sequence, flowReferenceNode1, null);

        // Then
        JSONObject serializedObject = sequence.getJSONObject(0);

        String actualJson = serializedObject.toString(2);
        String expectedJson = Json.FlowReference.Sample.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }
}

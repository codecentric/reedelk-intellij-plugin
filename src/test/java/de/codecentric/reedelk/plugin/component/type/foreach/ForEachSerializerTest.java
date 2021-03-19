package de.codecentric.reedelk.plugin.component.type.foreach;

import de.codecentric.reedelk.module.descriptor.model.property.DynamicValueDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import de.codecentric.reedelk.plugin.fixture.Json;
import de.codecentric.reedelk.plugin.AbstractGraphTest;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.runtime.api.script.dynamicvalue.DynamicObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Collections;

import static org.mockito.Mockito.doReturn;

class ForEachSerializerTest extends AbstractGraphTest {

    private ForEachSerializer serializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        serializer = new ForEachSerializer();
    }

    @Test
    void shouldCorrectlySerializeForEach() {
        // Given
        ComponentData componentData = forEachNode1.componentData();
        componentData.set("collection", "#[['one','two','three']]");

        DynamicValueDescriptor descriptor = new DynamicValueDescriptor();
        descriptor.setType(DynamicObject.class);

        PropertyDescriptor collectionProperty = PropertyDescriptor.builder()
                .name("collection")
                .type(descriptor)
                .build();

        doReturn(Collections.singletonList(collectionProperty)).when(componentData).getPropertiesDescriptors();

        JSONArray sequence = new JSONArray();

        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, forEachNode1);

        graph.add(forEachNode1, componentNode3);
        graph.add(componentNode3, componentNode2);
        graph.add(componentNode2, componentNode4);

        forEachNode1.addToScope(componentNode3);
        forEachNode1.addToScope(componentNode2);

        // When
        serializer.serialize(graph, sequence, forEachNode1, componentNode4);

        // Then
        JSONObject serializedObject = sequence.getJSONObject(0);

        String actualJson = serializedObject.toString(2);
        String expectedJson = Json.ForEach.Sample.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }
}

package com.esb.plugin.component.generic;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.ComponentPropertyDescriptor;
import com.esb.plugin.component.PrimitiveTypeDescriptor;
import com.esb.plugin.fixture.ComponentNode1;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import static com.esb.plugin.fixture.Json.GenericComponent;
import static java.util.Arrays.asList;

public class GenericComponentSerializerTest extends AbstractGraphTest {

    private GenericComponentSerializer serializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        serializer = new GenericComponentSerializer();
    }

    @Test
    void shouldCorrectlySerializeGenericComponent() {
        // Given
        JSONArray sequence = new JSONArray();
        ComponentData componentData = new ComponentData(ComponentDescriptor.create()
                .propertyDefinitions(asList(
                        createPropertyDefinition("property1", String.class),
                        createPropertyDefinition("property2", String.class),
                        createPropertyDefinition("property3", String.class)))
                .fullyQualifiedName(ComponentNode1.class.getName())
                .build());
        GraphNode genericComponent = new GenericComponentNode(componentData);
        componentData.set("property1", "first property");
        componentData.set("property2", "second property");
        componentData.set("property3", "third property");

        FlowGraph graph = graphProvider.createGraph();
        graph.root(root);
        graph.add(root, genericComponent);

        // When
        serializer.serialize(graph, sequence, genericComponent, null);

        // Then
        JSONObject serializedObject = sequence.getJSONObject(0);

        String actualJson = serializedObject.toString(2);
        String expectedJson = GenericComponent.Sample.json();
        JSONAssert.assertEquals(expectedJson, actualJson, true);
    }

    // Fixme
    private ComponentPropertyDescriptor createPropertyDefinition(String propertyName, Class<?> propertyClass) {
        return new ComponentPropertyDescriptor(
                propertyName,
                new PrimitiveTypeDescriptor(propertyClass),
                "A property name",
                "",
                true);
    }
}

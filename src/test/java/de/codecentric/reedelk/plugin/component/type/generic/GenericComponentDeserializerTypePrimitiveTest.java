package de.codecentric.reedelk.plugin.component.type.generic;

import de.codecentric.reedelk.plugin.assertion.PluginAssertion;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentDescriptor;
import de.codecentric.reedelk.plugin.fixture.ComponentNode1;
import de.codecentric.reedelk.plugin.fixture.Json;
import de.codecentric.reedelk.plugin.graph.deserializer.AbstractNodeDeserializerTest;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static java.util.Arrays.asList;

public class GenericComponentDeserializerTypePrimitiveTest extends AbstractNodeDeserializerTest {

    @Test
    void shouldCorrectlyDeserializePrimitiveTypesValues() {
        // Given
        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject genericComponentDefinition = new JSONObject(Json.GenericComponent.Primitives.json());

        // When
        GenericComponentDeserializer deserializer = new GenericComponentDeserializer(graph, node, context);
        GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("integerProperty", 234923)
                .hasDataWithValue("integerObjectProperty", Integer.valueOf("998829743"))
                .hasDataWithValue("longProperty", 913281L)
                .hasDataWithValue("longObjectProperty", Long.valueOf("55663"))
                .hasDataWithValue("floatProperty", 123.234f)
                .hasDataWithValue("floatObjectProperty", Float.parseFloat("7843.12"))
                .hasDataWithValue("doubleProperty", 234.234d)
                .hasDataWithValue("doubleObjectProperty", Double.parseDouble("11.88877"))
                .hasDataWithValue("booleanProperty", true)
                .hasDataWithValue("booleanObjectProperty", Boolean.TRUE)
                .hasDataWithValue("stringProperty", "my text sample")
                .hasDataWithValue("bigIntegerProperty", new BigInteger("88923423423"))
                .hasDataWithValue("bigDecimalProperty", new BigDecimal(1.001))
                .and().nodesCountIs(2);
    }

    private ComponentDescriptor descriptor =
            ComponentDescriptor.create().properties(asList(
                    SamplePropertyDescriptors.Primitives.integerProperty,
                    SamplePropertyDescriptors.Primitives.integerObjectProperty,
                    SamplePropertyDescriptors.Primitives.longProperty,
                    SamplePropertyDescriptors.Primitives.longObjectProperty,
                    SamplePropertyDescriptors.Primitives.floatProperty,
                    SamplePropertyDescriptors.Primitives.floatObjectProperty,
                    SamplePropertyDescriptors.Primitives.doubleProperty,
                    SamplePropertyDescriptors.Primitives.doubleObjectProperty,
                    SamplePropertyDescriptors.Primitives.booleanProperty,
                    SamplePropertyDescriptors.Primitives.booleanObjectProperty,
                    SamplePropertyDescriptors.Primitives.stringProperty,
                    SamplePropertyDescriptors.Primitives.bigIntegerProperty,
                    SamplePropertyDescriptors.Primitives.bigDecimalProperty))
                    .fullyQualifiedName(ComponentNode1.class.getName())
                    .build();
}

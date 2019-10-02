package com.reedelk.plugin.component.type.generic;

import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.component.domain.ComponentDefaultDescriptor;
import com.reedelk.plugin.component.domain.ComponentDescriptor;
import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializerTest;
import com.reedelk.plugin.graph.node.GraphNode;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.math.BigInteger;

import static com.reedelk.plugin.fixture.Json.GenericComponent;
import static java.util.Arrays.asList;

@MockitoSettings(strictness = Strictness.LENIENT)
class GenericComponentDeserializerTest extends AbstractNodeDeserializerTest {

    private GenericComponentDeserializer deserializer;

    private ComponentDescriptor descriptor = ComponentDefaultDescriptor.create()
            .propertyDescriptors(asList(
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

    @BeforeEach
    protected void setUp() {
        super.setUp();
        deserializer = new GenericComponentDeserializer(graph, context);
    }

    @Test
    void shouldDeserializeGenericComponentCorrectly() {
        // Given
        GenericComponentNode node = createGraphNodeInstance(GenericComponentNode.class, descriptor);

        mockContextInstantiateGraphNode(node);

        JSONObject genericComponentDefinition = new JSONObject(GenericComponent.Primitives.json());

        // When
        GraphNode lastNode = deserializer.deserialize(root, genericComponentDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(node)
                .hasDataWithValue("integerProperty", 234923)
                .hasDataWithValue("integerObjectProperty", new Integer("998829743"))
                .hasDataWithValue("longProperty", 913281L)
                .hasDataWithValue("longObjectProperty", new Long("55663"))
                .hasDataWithValue("floatProperty", 123.234f)
                .hasDataWithValue("floatObjectProperty", new Float("7843.12"))
                .hasDataWithValue("doubleProperty", 234.234d)
                .hasDataWithValue("doubleObjectProperty", new Double("11.88877"))
                .hasDataWithValue("booleanProperty", true)
                .hasDataWithValue("booleanObjectProperty", Boolean.FALSE)
                .hasDataWithValue("stringProperty", "my text sample")
                .hasDataWithValue("bigIntegerProperty", new BigInteger("88923423423"))
                .hasDataWithValue("bigDecimalProperty", new BigDecimal("1.001"))
                .and().nodesCountIs(2);
    }
}

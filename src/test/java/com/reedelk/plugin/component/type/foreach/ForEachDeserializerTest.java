package com.reedelk.plugin.component.type.foreach;

import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeDynamicValueDescriptor;
import com.reedelk.plugin.assertion.PluginAssertion;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.graph.deserializer.AbstractNodeDeserializerTest;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.runtime.api.script.dynamicvalue.DynamicObject;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static com.reedelk.plugin.fixture.Json.ForEach;
import static org.mockito.Mockito.doReturn;

class ForEachDeserializerTest extends AbstractNodeDeserializerTest {

    private ForEachDeserializer deserializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        deserializer = new ForEachDeserializer(graph, forEachNode1, context);
        ComponentData componentData = forEachNode1.componentData();

        TypeDynamicValueDescriptor descriptor = new TypeDynamicValueDescriptor();
        descriptor.setType(DynamicObject.class);

        PropertyDescriptor collectionProperty = PropertyDescriptor.builder()
                .name("collection")
                .type(descriptor)
                .build();

        doReturn(Collections.singletonList(collectionProperty)).when(componentData).getPropertiesDescriptors();
    }

    @Test
    void shouldDeserializeForEachDefinitionCorrectly() {
        // Given
        JSONObject forkDefinition = new JSONObject(ForEach.Sample.json());

        // When
        GraphNode lastNode = deserializer.deserialize(root, forkDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(stopNode1)
                .and().successorsOf(forEachNode1).isOnly(componentNode3)
                .and().successorsOf(componentNode3).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isOnly(stopNode1)
                .and().node(forEachNode1).hasDataWithValue("collection", "#[['one', 'two', 'three']]")
                .and().successorsOf(stopNode1).isEmpty();
    }
}

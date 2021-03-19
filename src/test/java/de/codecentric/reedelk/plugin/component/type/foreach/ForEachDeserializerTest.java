package de.codecentric.reedelk.plugin.component.type.foreach;

import de.codecentric.reedelk.plugin.assertion.PluginAssertion;
import de.codecentric.reedelk.module.descriptor.model.property.DynamicValueDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import de.codecentric.reedelk.plugin.fixture.Json;
import de.codecentric.reedelk.plugin.component.ComponentData;
import de.codecentric.reedelk.plugin.graph.deserializer.AbstractNodeDeserializerTest;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.runtime.api.script.dynamicvalue.DynamicObject;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.mockito.Mockito.doReturn;

class ForEachDeserializerTest extends AbstractNodeDeserializerTest {

    private ForEachDeserializer deserializer;

    @BeforeEach
    protected void setUp() {
        super.setUp();
        deserializer = new ForEachDeserializer(graph, forEachNode1, context);
        ComponentData componentData = forEachNode1.componentData();

        DynamicValueDescriptor descriptor = new DynamicValueDescriptor();
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
        JSONObject forkDefinition = new JSONObject(Json.ForEach.Sample.json());

        // When
        GraphNode lastNode = deserializer.deserialize(root, forkDefinition);

        // Then
        PluginAssertion.assertThat(graph)
                .node(lastNode).is(stopNode1)
                .and().successorsOf(forEachNode1).isOnly(componentNode3)
                .and().successorsOf(componentNode3).isOnly(componentNode2)
                .and().successorsOf(componentNode2).isOnly(stopNode1)
                .and().node(forEachNode1).hasDataWithValue("collection", "#[['one','two','three']]")
                .and().successorsOf(stopNode1).isEmpty();
    }
}

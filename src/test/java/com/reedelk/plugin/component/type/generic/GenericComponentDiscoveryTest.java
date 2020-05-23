package com.reedelk.plugin.component.type.generic;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.fixture.ComponentNode2;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.metadata.DiscoveryStrategy;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutputCompound;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutputDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.MessageAttributeType;
import static com.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils.MyItemType;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class GenericComponentDiscoveryTest extends AbstractGraphTest {

    @Mock
    private Module module;
    @Mock
    private PlatformModuleService moduleService;

    private GenericComponentDiscovery discovery;
    private TypeAndTries typeAndTries;
    private FlowGraph graph;

    @BeforeEach
    public void setUp() {
        super.setUp();
        graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);
        graph.add(componentNode1, componentNode2);
        graph.add(componentNode2, componentNode3);

        typeAndTries = new TypeAndTries();
        discovery = new GenericComponentDiscovery(module, moduleService, typeAndTries);
    }

    @Test
    void shouldReturnDefaultPreviousComponentOutputWhenComponentDescriptorOutputIsNull() {
        // Given
        ComponentContext componentContext = mockComponentContext(null);

        // When
        Optional<PreviousComponentOutput> maybeActualOutput =
                discovery.compute(componentContext, componentNode2);

        // Then
        assertThat(maybeActualOutput).contains(DiscoveryStrategy.DEFAULT);
    }

    @Test
    void shouldReturnCorrectCompoundComponentOutput() {
        // Given
        List<String> payload = singletonList(MyItemType.class.getName());
        List<String> attributes = singletonList(MessageAttributeType.class.getName());
        String description = "My test description";

        ComponentOutputDescriptor outputDescriptor = new ComponentOutputDescriptor();
        outputDescriptor.setDescription(description);
        outputDescriptor.setAttributes(attributes);
        outputDescriptor.setPayload(payload);

        ComponentContext componentContext = mockComponentContext(outputDescriptor);

        // When
        Optional<PreviousComponentOutput> maybeActualOutput =
                discovery.compute(componentContext, componentNode2);

        // Then
        PreviousComponentOutputDefault expectedPayloadOutput =
                new PreviousComponentOutputDefault(attributes, payload, description);
        PreviousComponentOutputDefault expectedAttributeOutput =
                new PreviousComponentOutputDefault(attributes, payload, description);
        PreviousComponentOutputCompound expectedPreviousOutput  =
                new PreviousComponentOutputCompound(expectedAttributeOutput, expectedPayloadOutput);
        assertThat(maybeActualOutput).contains(expectedPreviousOutput);
    }


    private ComponentContext mockComponentContext(ComponentOutputDescriptor outputDescriptor) {
        ComponentDescriptor descriptor = new ComponentDescriptor();
        descriptor.setOutput(outputDescriptor);
        doReturn(descriptor)
                .when(moduleService)
                .componentDescriptorOf(ComponentNode2.class.getName());
        return new ComponentContext(graph, componentNode3);
    }
}

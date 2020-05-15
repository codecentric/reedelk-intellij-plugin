package com.reedelk.plugin.component.type.generic;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class GenericComponentDiscoveryTest extends AbstractGraphTest {

    @Mock
    private Module module;
    @Mock
    private PlatformModuleService moduleService;

    private TypeAndTries typeAndTries;

    private GenericComponentDiscovery discovery;
    private FlowGraph graph;

    @BeforeEach
    public void setUp() {
        super.setUp();
        graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);
        graph.add(componentNode1, componentNode2);

        typeAndTries = new TypeAndTries();
        discovery = new GenericComponentDiscovery(module, moduleService, typeAndTries);
    }

    @Test
    void shouldReturnDefaultOutputWhenComponentOutputDescriptorIsNull() {
        // Given (component 1 is the predecessor of the current component we want to know the previous otuput from)
        ComponentDescriptor descriptor = new ComponentDescriptor();
        doReturn(descriptor)
                .when(moduleService)
                .componentDescriptorOf(ComponentNode1.class.getName());
        ComponentContext componentContext = new ComponentContext(graph, componentNode2);

        // When
        Optional<? extends ComponentOutputDescriptor> maybeActualOutput =
                discovery.compute(componentContext, componentNode1);

        // Then
        assertThat(maybeActualOutput).isPresent();

        ComponentOutputDescriptor outputDescriptor = maybeActualOutput.get();
        assertThat(outputDescriptor.getAttributes()).isEqualTo(MessageAttributes.class.getName());
        assertThat(outputDescriptor.getPayload()).isEqualTo(singletonList(Object.class.getName()));
        assertThat(outputDescriptor.getDescription()).isNull();
    }
}

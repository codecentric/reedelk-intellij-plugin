package com.reedelk.plugin.component.type.generic;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.fixture.ComponentNode1;
import com.reedelk.plugin.fixture.ComponentNode2;
import com.reedelk.plugin.fixture.ComponentRoot;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.runtime.api.annotation.ComponentOutput;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static java.util.Arrays.asList;
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
        graph.add(componentNode2, componentNode3);

        typeAndTries = new TypeAndTries();
        discovery = new GenericComponentDiscovery(module, moduleService, typeAndTries);
    }

    @Test
    void shouldReturnDefaultOutputWhenComponentOutputDescriptorIsNull() {
        // Given (component 1 is the predecessor of the current component we want to know the previous otuput from)
        ComponentDescriptor descriptor = new ComponentDescriptor();
        doReturn(descriptor)
                .when(moduleService)
                .componentDescriptorOf(ComponentNode2.class.getName());
        ComponentContext componentContext = new ComponentContext(graph, componentNode3);

        // When
        Optional<? extends ComponentOutputDescriptor> maybeActualOutput =
                discovery.compute(componentContext, componentNode2);

        // Then
        assertThat(maybeActualOutput).isPresent();

        ComponentOutputDescriptor outputDescriptor = maybeActualOutput.get();
        assertThat(outputDescriptor.getDescription()).isNull();
        assertThat(outputDescriptor.getAttributes()).isEqualTo(MessageAttributes.class.getName());
        assertThat(outputDescriptor.getPayload()).isEqualTo(singletonList(Object.class.getName()));
    }

    @Test
    void shouldReturnOutputWhenComponentOutputDescriptorIsDefined() {
        // Given
        ComponentDescriptor descriptor =
                createComponentDescriptor("com.test.MyAttributes", "My description", "com.test.MyType1", "com.test.MyType2");
        doReturn(descriptor)
                .when(moduleService)
                .componentDescriptorOf(ComponentNode2.class.getName());
        ComponentContext componentContext = new ComponentContext(graph, componentNode3);

        // When
        Optional<? extends ComponentOutputDescriptor> maybeActualOutput =
                discovery.compute(componentContext, componentNode2);

        // Then
        assertThat(maybeActualOutput).isPresent();

        ComponentOutputDescriptor actualOutputDescriptor = maybeActualOutput.get();
        assertThat(actualOutputDescriptor.getDescription()).isEqualTo("My description");
        assertThat(actualOutputDescriptor.getAttributes()).isEqualTo("com.test.MyAttributes");
        assertThat(actualOutputDescriptor.getPayload()).isEqualTo(asList("com.test.MyType1", "com.test.MyType2"));
    }

    @Test
    void shouldReturnOutputPayloadFromPreviousComponent() {
        // Given
        ComponentDescriptor descriptor =
                createComponentDescriptor("com.test.MyAttributes", "My description", ComponentOutput.PreviousComponent.class.getName());
        doReturn(descriptor)
                .when(moduleService)
                .componentDescriptorOf(ComponentNode2.class.getName());

        ComponentDescriptor previousDescriptor =
                createComponentDescriptor("com.test.MyOtherAttributes", "My other description", "com.test.MyType1");
        doReturn(previousDescriptor)
                .when(moduleService)
                .componentDescriptorOf(ComponentNode1.class.getName());


        ComponentContext componentContext = new ComponentContext(graph, componentNode3);

        // When
        Optional<? extends ComponentOutputDescriptor> maybeActualOutput =
                discovery.compute(componentContext, componentNode2);

        // Then
        assertThat(maybeActualOutput).isPresent();

        ComponentOutputDescriptor actualOutputDescriptor = maybeActualOutput.get();
        assertThat(actualOutputDescriptor.getDescription()).isEqualTo("My other description"); // description from component 1
        assertThat(actualOutputDescriptor.getAttributes()).isEqualTo("com.test.MyAttributes"); // attributes from component 2
        assertThat(actualOutputDescriptor.getPayload()).isEqualTo(singletonList("com.test.MyType1")); // payload from component 1
    }

    @Test
    void shouldReturnOutputAttributesFromPreviousComponent() {
        // Given
        ComponentDescriptor descriptor =
                createComponentDescriptor(ComponentOutput.PreviousComponent.class.getName(), "My description", String.class.getName());
        doReturn(descriptor)
                .when(moduleService)
                .componentDescriptorOf(ComponentNode2.class.getName());

        ComponentDescriptor previousDescriptor =
                createComponentDescriptor("com.test.MyOtherAttributes", "My other description", "com.test.MyType1");
        doReturn(previousDescriptor)
                .when(moduleService)
                .componentDescriptorOf(ComponentNode1.class.getName());


        ComponentContext componentContext = new ComponentContext(graph, componentNode3);

        // When
        Optional<? extends ComponentOutputDescriptor> maybeActualOutput =
                discovery.compute(componentContext, componentNode2);

        // Then
        assertThat(maybeActualOutput).isPresent();

        ComponentOutputDescriptor actualOutputDescriptor = maybeActualOutput.get();
        assertThat(actualOutputDescriptor.getDescription()).isEqualTo("My description"); // description from component 1
        assertThat(actualOutputDescriptor.getAttributes()).isEqualTo("com.test.MyOtherAttributes"); // attributes from component 2
        assertThat(actualOutputDescriptor.getPayload()).isEqualTo(singletonList(String.class.getName())); // payload from component 1
    }

    @Test
    void shouldReturnDefaultDescriptorWhenPreviousIsRootAndPayloadIsPreviousComponent() {
        // Given
        ComponentDescriptor previousDescriptor =
                createComponentDescriptor("com.test.MyAttributes", "My description", ComponentOutput.PreviousComponent.class.getName());
        doReturn(previousDescriptor)
                .when(moduleService)
                .componentDescriptorOf(ComponentRoot.class.getName());


        ComponentContext componentContext = new ComponentContext(graph, componentNode1);

        // When
        Optional<? extends ComponentOutputDescriptor> maybeActualOutput =
                discovery.compute(componentContext, root);

        // Then
        assertThat(maybeActualOutput).isNotPresent();

        ComponentOutputDescriptor actualOutputDescriptor = maybeActualOutput.get();
        assertThat(actualOutputDescriptor.getDescription()).isEqualTo("My description");
        assertThat(actualOutputDescriptor.getAttributes()).isEqualTo("com.test.MyAttributes");
        assertThat(actualOutputDescriptor.getPayload()).isEqualTo(singletonList(Object.class.getName()));
    }

    private ComponentDescriptor createComponentDescriptor(String attribute, String description, String ...payload) {
        ComponentOutputDescriptor outputDescriptor = new ComponentOutputDescriptor();
        outputDescriptor.setDescription(description);
        outputDescriptor.setPayload(asList(payload));
        outputDescriptor.setAttributes(attribute);
        ComponentDescriptor descriptor = new ComponentDescriptor();
        descriptor.setOutput(outputDescriptor);
        return descriptor;
    }
}

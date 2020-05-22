package com.reedelk.plugin.component.type.generic;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentDescriptor;
import com.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.fixture.ComponentNode2;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.completion.TypeAndTries;
import com.reedelk.plugin.service.module.impl.component.completion.TypeTestUtils;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutputCompound;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutputDefault;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenericComponentDiscoveryTest extends AbstractGraphTest {

    @Mock
    private Module module;
    @Mock
    private PlatformModuleService moduleService;

    private TypeAndTries typeAndTries;

    private TestableGenericComponentDiscovery discovery;
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
        discovery = spy(new TestableGenericComponentDiscovery(module, moduleService, typeAndTries));
        lenient()
                .doReturn(Optional.empty())
                .when(discovery);
               // .discover(any(ComponentContext.class), any(GraphNode.class));
    }

    @Test
    void shouldReturnDefaultOutputWhenComponentDescriptorOutputIsNull() {
        // Given
        ComponentDescriptor descriptor = new ComponentDescriptor();
        doReturn(descriptor)
                .when(moduleService)
                .componentDescriptorOf(ComponentNode2.class.getName());
        ComponentContext componentContext = new ComponentContext(graph, componentNode3);

        // When
        Optional<PreviousComponentOutput> maybeActualOutput =
                discovery.compute(componentContext, componentNode2);

        // Then
        assertThat(maybeActualOutput).isPresent();

        PreviousComponentOutput previousOutput = maybeActualOutput.get();
        assertThat(previousOutput).isInstanceOf(PreviousComponentOutputDefault.class);
    }

    @Test
    void shouldReturnCompound() {
        // Given
        ComponentOutputDescriptor outputDescriptor = new ComponentOutputDescriptor();
        outputDescriptor.setPayload(singletonList(TypeTestUtils.MyItemType.class.getName()));

        ComponentDescriptor descriptor = new ComponentDescriptor();
        descriptor.setOutput(outputDescriptor);

        doReturn(descriptor)
                .when(moduleService)
                .componentDescriptorOf(ComponentNode2.class.getName());
        ComponentContext componentContext = new ComponentContext(graph, componentNode3);

        // When
        Optional<PreviousComponentOutput> maybeActualOutput =
                discovery.compute(componentContext, componentNode2);

        // Then
        assertThat(maybeActualOutput).isPresent();

        PreviousComponentOutput previousOutput = maybeActualOutput.get();
        assertThat(previousOutput).isInstanceOf(PreviousComponentOutputCompound.class);

        // TODO: Continue
    }

    /**
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
        assertThat(actualOutputDescriptor.getPayload()).isEqualTo(asList("com.test.MyType1", "com.test.MyType2"));
        assertThat(actualOutputDescriptor.getAttributes()).isEqualTo(singletonList("com.test.MyAttributes"));
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
        assertThat(actualOutputDescriptor.getPayload()).isEqualTo(singletonList("com.test.MyType1")); // payload from component 1
        assertThat(actualOutputDescriptor.getAttributes()).isEqualTo(singletonList("com.test.MyAttributes")); // attributes from component 2
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
        assertThat(actualOutputDescriptor.getPayload()).isEqualTo(singletonList(String.class.getName())); // payload from component 1
        assertThat(actualOutputDescriptor.getAttributes()).isEqualTo(singletonList("com.test.MyOtherAttributes")); // attributes from component 2
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
        assertThat(maybeActualOutput).isPresent();

        ComponentOutputDescriptor actualOutputDescriptor = maybeActualOutput.get();
        assertThat(actualOutputDescriptor.getDescription()).isEqualTo(""); // the description is taken from the predecessor of root (which is missing)
        assertThat(actualOutputDescriptor.getAttributes()).isEqualTo(singletonList("com.test.MyAttributes"));
        assertThat(actualOutputDescriptor.getPayload()).isEqualTo(singletonList(Object.class.getName()));
    }

    private ComponentDescriptor createComponentDescriptor(String attribute, String description, String ...payload) {
        ComponentOutputDescriptor outputDescriptor = new ComponentOutputDescriptor();
        outputDescriptor.setAttributes(singletonList(attribute));
        outputDescriptor.setDescription(description);
        outputDescriptor.setPayload(asList(payload));
        ComponentDescriptor descriptor = new ComponentDescriptor();
        descriptor.setOutput(outputDescriptor);
        return descriptor;
    }*/

    // We want to spy the discover, that's we we are extending it.
    static class TestableGenericComponentDiscovery extends GenericComponentDiscovery {

        public TestableGenericComponentDiscovery(Module module, PlatformModuleService moduleService, TypeAndTries typeAndAndTries) {
            super(module, moduleService, typeAndAndTries);
        }

        @Override
        public Optional<PreviousComponentOutput> compute(ComponentContext context, GraphNode currentNode) {
            return super.compute(context, currentNode);
        }
    }
}

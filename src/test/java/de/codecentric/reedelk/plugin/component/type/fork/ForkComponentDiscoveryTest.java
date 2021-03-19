package de.codecentric.reedelk.plugin.component.type.fork;

import de.codecentric.reedelk.plugin.fixture.*;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.*;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentDescriptor;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentType;
import de.codecentric.reedelk.plugin.component.type.generic.GenericComponentNode;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.service.module.impl.component.ComponentContext;
import de.codecentric.reedelk.runtime.api.component.Join;
import de.codecentric.reedelk.runtime.api.flow.FlowContext;
import de.codecentric.reedelk.runtime.api.message.Message;
import de.codecentric.reedelk.runtime.api.message.MessageAttributes;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.AbstractComponentDiscoveryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class ForkComponentDiscoveryTest extends AbstractComponentDiscoveryTest {

    private ForkComponentDiscovery discovery;
    private GraphNode joinComponent;

    @BeforeEach
    public void setUp() {
        super.setUp();
        ComponentDescriptor descriptor = new ComponentDescriptor();
        descriptor.setType(ComponentType.JOIN);
        joinComponent =
                createGraphNodeInstance(MyJoin.class, GenericComponentNode.class, ComponentType.JOIN);

        graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);
        graph.add(componentNode1, forkNode1);
        graph.add(forkNode1, componentNode2);
        graph.add(forkNode1, componentNode3);
        graph.add(componentNode2, componentNode4);
        graph.add(componentNode3, componentNode4);
        graph.add(componentNode4, forkNode2);
        graph.add(forkNode2, componentNode5);
        graph.add(forkNode2, componentNode6);
        graph.add(componentNode5, joinComponent);
        graph.add(componentNode6, joinComponent);

        forkNode1.addToScope(componentNode2);
        forkNode1.addToScope(componentNode3);
        forkNode2.addToScope(componentNode5);
        forkNode2.addToScope(componentNode6);

        discovery = spy(new ForkComponentDiscovery(module, moduleService, typeAndTries));
    }

    @Test
    void shouldReturnForkPreviousComponentOutput() {
        // Given
        PreviousComponentOutput previousPreviousComponentOutput = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                asList(MyTestType.class.getName(), String.class.getName()),
                "Description of the previous");

        ComponentContext componentContext = mockComponentContext(null);

        doReturn(Optional.of(previousPreviousComponentOutput))
                .when(discovery)
                .discover(componentContext, forkNode1);

        // When
        Optional<PreviousComponentOutput> maybeActualOutput =
                discovery.compute(componentContext, forkNode1, componentNode2);

        // Then
        assertThat(maybeActualOutput).contains(previousPreviousComponentOutput);
    }

    @Test
    void shouldReturnForkPreviousComponentOutputMultipleMessagesWhenNodeFollowingForkIsJoin() {
        // Given (joining node is JOIN)
        ComponentOutputDescriptor node6Output = new ComponentOutputDescriptor();
        node6Output.setPayload(singletonList(Long.class.getName()));
        node6Output.setAttributes(singletonList(MessageAttributes.class.getName()));
        node6Output.setDescription("My description 6");
        mockComponentContext(node6Output, ComponentNode6.class, componentNode6);

        ComponentOutputDescriptor node5Output = new ComponentOutputDescriptor();
        node5Output.setPayload(singletonList(String.class.getName()));
        node5Output.setAttributes(singletonList(MessageAttributes.class.getName()));
        node5Output.setDescription("My description 5");
        mockComponentContext(node5Output, ComponentNode5.class, componentNode5);

        ComponentContext componentContext = mockComponentContext(null);
        doReturn(Optional.of(joinComponent))
                .when(componentContext)
                .findFirstNodeOutsideCurrentScope(forkNode2);

        // When
        Optional<PreviousComponentOutput> maybeActualOutput =
                discovery.compute(componentContext, forkNode2);

        // Then
        assertThat(maybeActualOutput.get())
                .isInstanceOf(PreviousComponentOutputMultipleMessages.class);
    }

    @Test
    void shouldReturnPreviousComponentOutputJoinWithCorrectComponentOutputs() {
        // Given
        ComponentOutputDescriptor node2Output = new ComponentOutputDescriptor();
        node2Output.setPayload(singletonList(Long.class.getName()));
        node2Output.setAttributes(singletonList(MessageAttributes.class.getName()));
        node2Output.setDescription("My description 2");
        mockComponentContext(node2Output, ComponentNode2.class, componentNode2);

        ComponentOutputDescriptor node3Output = new ComponentOutputDescriptor();
        node3Output.setPayload(singletonList(String.class.getName()));
        node3Output.setAttributes(singletonList(MessageAttributes.class.getName()));
        node3Output.setDescription("My description 3");
        mockComponentContext(node3Output, ComponentNode3.class, componentNode3);

        // Given (joining node is not JOIN)
        ComponentContext componentContext = mockComponentContext(null, ComponentNode4.class, componentNode4);
        doReturn(Optional.of(componentNode4))
                .when(componentContext)
                .findFirstNodeOutsideCurrentScope(forkNode1);

        // When
        Optional<PreviousComponentOutput> maybeActualOutput =
                discovery.compute(componentContext, forkNode1);

        PreviousComponentOutputDefault output2Payload = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(Long.class.getName()),
                "My description 2");
        PreviousComponentOutputDefault output2Attribute = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(Long.class.getName()),
                "My description 2");

        PreviousComponentOutput outputDefault2 =
                new PreviousComponentOutputCompound(output2Attribute, output2Payload);


        PreviousComponentOutputDefault output3Payload = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(String.class.getName()),
                "My description 3");

        PreviousComponentOutputDefault output3Attribute = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                singletonList(String.class.getName()),
                "My description 3");

        PreviousComponentOutput outputDefault3 =
                new PreviousComponentOutputCompound(output3Attribute, output3Payload);

        Set<PreviousComponentOutput> expectedOutputs = new HashSet<>();
        expectedOutputs.add(outputDefault2);
        expectedOutputs.add(outputDefault3);

        PreviousComponentOutputJoin expectedOutput =
                new PreviousComponentOutputJoin(expectedOutputs);

        // Then
        assertThat(maybeActualOutput).contains(expectedOutput);
    }


    static class MyJoin implements Join {
        @Override
        public Message apply(FlowContext flowContext, List<Message> list) {
            throw new UnsupportedOperationException();
        }
    }
}

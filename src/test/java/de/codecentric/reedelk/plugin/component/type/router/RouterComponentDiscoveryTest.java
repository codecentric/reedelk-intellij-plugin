package de.codecentric.reedelk.plugin.component.type.router;

import de.codecentric.reedelk.plugin.fixture.ComponentNode4;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.*;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentOutputDescriptor;
import de.codecentric.reedelk.plugin.fixture.ComponentNode2;
import de.codecentric.reedelk.plugin.fixture.ComponentNode3;
import de.codecentric.reedelk.plugin.service.module.impl.component.ComponentContext;
import de.codecentric.reedelk.runtime.api.message.MessageAttributes;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.AbstractComponentDiscoveryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class RouterComponentDiscoveryTest extends AbstractComponentDiscoveryTest {

    private RouterComponentDiscovery discovery;

    @BeforeEach
    public void setUp() {
        super.setUp();
        graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);
        graph.add(componentNode1, routerNode1);
        graph.add(routerNode1, componentNode2);
        graph.add(routerNode1, componentNode3);
        graph.add(componentNode2, componentNode4);
        graph.add(componentNode3, componentNode4);

        routerNode1.addToScope(componentNode2);
        routerNode1.addToScope(componentNode3);

        discovery = spy(new RouterComponentDiscovery(module, moduleService, typeAndTries));
    }

    @Test
    void shouldReturnRouterPreviousComponentOutput() {
        // Given
        PreviousComponentOutput previousPreviousComponentOutput = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                asList(MyTestType.class.getName(), String.class.getName()),
                "Description of the previous");

        ComponentContext componentContext = mockComponentContext(null);

        doReturn(Optional.of(previousPreviousComponentOutput))
                .when(discovery)
                .discover(componentContext, routerNode1);

        // When
        Optional<PreviousComponentOutput> maybeActualOutput =
                discovery.compute(componentContext, routerNode1, componentNode3);

        // Then
        assertThat(maybeActualOutput).contains(previousPreviousComponentOutput);
    }

    @Test
    void shouldReturnPreviousComponentOutputOneOfWithCorrectComponentOutputs() {
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

        // Given
        ComponentContext componentContext = mockComponentContext(null, ComponentNode4.class, componentNode4);

        // When
        Optional<PreviousComponentOutput> maybeActualOutput =
                discovery.compute(componentContext, routerNode1);

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

        PreviousComponentOutputOneOf expectedOutput =
                new PreviousComponentOutputOneOf(expectedOutputs);

        // Then
        assertThat(maybeActualOutput).contains(expectedOutput);
    }
}

package com.reedelk.plugin.component.type.fork;

import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.metadata.AbstractComponentDiscoveryTest;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutputDefault;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

class ForkComponentDiscoveryTest extends AbstractComponentDiscoveryTest {

    private ForkComponentDiscovery discovery;

    @BeforeEach
    public void setUp() {
        super.setUp();
        graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);
        graph.add(componentNode1, forkNode1);
        graph.add(forkNode1, componentNode2);
        graph.add(forkNode1, componentNode3);
        graph.add(componentNode2, componentNode4);
        graph.add(componentNode3, componentNode4);

        forkNode1.addToScope(componentNode2);
        forkNode1.addToScope(componentNode3);

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
                discovery.compute(componentContext, (GraphNode) forkNode1);

        // Then
        assertThat(maybeActualOutput).contains(previousPreviousComponentOutput);
    }

    @Test
    void shouldReturnForkPrevious() {

    }
}

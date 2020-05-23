package com.reedelk.plugin.component.type.flowreference;

import com.reedelk.plugin.service.module.impl.component.metadata.AbstractComponentDiscoveryTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.spy;

class FlowReferenceComponentDiscoveryTest extends AbstractComponentDiscoveryTest {

    private FlowReferenceComponentDiscovery discovery;

    @BeforeEach
    public void setUp() {
        super.setUp();

        graph = provider.createGraph();
        graph.root(root);
        graph.add(root, componentNode1);
        graph.add(componentNode1, flowReferenceNode1);
        graph.add(flowReferenceNode1, componentNode2);

        discovery = spy(new FlowReferenceComponentDiscovery(module, moduleService, typeAndTries));
    }

    @Test
    void shouldReturnCorrectForEachPreviousComponentOutput() {
        // Given

        // When

        // Then
    }
}

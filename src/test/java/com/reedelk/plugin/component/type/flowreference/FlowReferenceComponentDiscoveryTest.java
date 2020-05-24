package com.reedelk.plugin.component.type.flowreference;

import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.module.impl.component.ComponentContext;
import com.reedelk.plugin.service.module.impl.component.metadata.AbstractComponentDiscoveryTest;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutput;
import com.reedelk.plugin.service.module.impl.component.metadata.PreviousComponentOutputDefault;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static com.reedelk.runtime.commons.JsonParser.FlowReference;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
        FlowGraph subflowGraph = provider.createGraph();
        subflowGraph.root(componentNode3);
        subflowGraph.add(componentNode3, componentNode4);

        String subflowReferenceId = "aabbcc";
        PreviousComponentOutput previousPreviousComponentOutput = new PreviousComponentOutputDefault(
                singletonList(MessageAttributes.class.getName()),
                asList(MyTestType.class.getName(), String.class.getName()),
                "Description of the previous");

        mockComponentData(flowReferenceNode1, FlowReference.ref(), subflowReferenceId);

        ComponentContext componentContext = mockComponentContext(null);

        doReturn(Optional.of(previousPreviousComponentOutput))
                .when(discovery)
                .discover(Mockito.any(ComponentContext.class), Mockito.eq(flowReferenceNode1));

        doReturn(Optional.of(subflowGraph))
                .when(discovery)
                .deserializeSubflowBy(subflowReferenceId);

        // When
        Optional<PreviousComponentOutput> maybeActualOutput =
                discovery.compute(componentContext, flowReferenceNode1, componentNode2);

        // Then
        assertThat(maybeActualOutput).contains(previousPreviousComponentOutput);

    }

    private void mockComponentData(GraphNode target, String propertyKey, Object propertyValue) {
        ComponentData componentData = mock(ComponentData.class);
        doReturn(componentData).when(target).componentData();
        doReturn(true).when(componentData).has(propertyKey);
        doReturn(propertyValue).when(componentData).get(propertyKey);
    }
}

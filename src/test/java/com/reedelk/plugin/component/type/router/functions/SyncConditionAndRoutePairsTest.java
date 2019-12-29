package com.reedelk.plugin.component.type.router.functions;

import com.reedelk.component.descriptor.ComponentData;
import com.reedelk.plugin.AbstractGraphTest;
import com.reedelk.plugin.component.type.router.RouterConditionRoutePair;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class SyncConditionAndRoutePairsTest extends AbstractGraphTest {

    @Test
    void shouldCorrectlyUpdatePairsWhenFollowingNodeRemoved() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(routerNode1, componentNode3);

        List<RouterConditionRoutePair> current = new ArrayList<>();
        current.add(new RouterConditionRoutePair("payload.name == 'John'", componentNode1));
        current.add(new RouterConditionRoutePair("payload.name == 'Mark'", componentNode2));
        current.add(new RouterConditionRoutePair("otherwise", componentNode3));

        mockRouterConditionRoutePairsWith(current);

        // When
        List<RouterConditionRoutePair> updatedPairs =
                SyncConditionAndRoutePairs.from(graph, routerNode1);

        // Then
        assertThat(updatedPairs).hasSize(2);
        assertThatExistsPairWith(updatedPairs, "payload.name == 'John'", componentNode1);
        assertThatExistsPairWith(updatedPairs, "otherwise", componentNode3);
    }

    @Test
    void shouldCorrectlyUpdatePairsWhenFollowingNodeAdded() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(routerNode1, componentNode2);
        graph.add(routerNode1, componentNode3);

        List<RouterConditionRoutePair> current = new ArrayList<>();
        current.add(new RouterConditionRoutePair("payload.name == 'John'", componentNode1));
        current.add(new RouterConditionRoutePair("otherwise", componentNode3));

        mockRouterConditionRoutePairsWith(current);

        // When
        List<RouterConditionRoutePair> updatedPairs =
                SyncConditionAndRoutePairs.from(graph, routerNode1);

        // Then
        assertThat(updatedPairs).hasSize(3);
        assertThatExistsPairWith(updatedPairs, "payload.name == 'John'", componentNode1);
        assertThatExistsPairWith(updatedPairs, "", componentNode2);
        assertThatExistsPairWith(updatedPairs, "otherwise", componentNode3);
    }

    @Test
    void shouldCorrectlyUpdatePairsWhenFollowingNodeReplacedAnotherNodeInSamePosition() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);
        graph.add(routerNode1, componentNode4);
        graph.add(routerNode1, componentNode3);

        List<RouterConditionRoutePair> current = new ArrayList<>();
        current.add(new RouterConditionRoutePair("payload.name == 'John'", componentNode1));
        current.add(new RouterConditionRoutePair("payload.name == 'Mark'", componentNode2));
        current.add(new RouterConditionRoutePair("otherwise", componentNode3));

        mockRouterConditionRoutePairsWith(current);

        // When
        List<RouterConditionRoutePair> updatedPairs =
                SyncConditionAndRoutePairs.from(graph, routerNode1);

        // Then
        assertThat(updatedPairs).hasSize(3);
        assertThatExistsPairWith(updatedPairs, "payload.name == 'John'", componentNode1);
        assertThatExistsPairWith(updatedPairs, "payload.name == 'Mark'", componentNode4);
        assertThatExistsPairWith(updatedPairs, "otherwise", componentNode3);
    }

    @Test
    void shouldCorrectlyUpdatePairsWhenOtherwiseReplacedByAnotherNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode2);
        graph.add(routerNode1, componentNode1);

        List<RouterConditionRoutePair> current = new ArrayList<>();
        current.add(new RouterConditionRoutePair("payload.name == 'John'", componentNode1));
        current.add(new RouterConditionRoutePair("otherwise", componentNode2));

        mockRouterConditionRoutePairsWith(current);

        // When
        List<RouterConditionRoutePair> updatedPairs =
                SyncConditionAndRoutePairs.from(graph, routerNode1);

        // Then
        assertThat(updatedPairs).hasSize(2);
        assertThatExistsPairWith(updatedPairs, "payload.name == 'John'", componentNode2);
        assertThatExistsPairWith(updatedPairs, "otherwise", componentNode1);
    }

    @Test
    void shouldSetDefaultOtherwiseConditionWhenOnlyOneSuccessor() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);

        List<RouterConditionRoutePair> current = new ArrayList<>();

        mockRouterConditionRoutePairsWith(current);

        List<RouterConditionRoutePair> updatedPairs =
                SyncConditionAndRoutePairs.from(graph, routerNode1);

        // When
        assertThat(updatedPairs).hasSize(1);
        assertThatExistsPairWith(updatedPairs, "otherwise", componentNode1);
    }

    @Test
    void shouldThrowExceptionWhenThereAreNoSuccessorsOfRouterNode() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);

        List<RouterConditionRoutePair> current = new ArrayList<>();
        current.add(new RouterConditionRoutePair("payload.name == 'John'", componentNode1));

        // When
        assertThrows(IllegalStateException.class,
                () -> SyncConditionAndRoutePairs.from(graph, routerNode1),
                "Expected at least one successor for router node but 0 were found");
    }

    private void assertThatExistsPairWith(List<RouterConditionRoutePair> pairs, String expectedCondition, GraphNode expectedNode) {
        for (RouterConditionRoutePair pair : pairs) {
            String condition = pair.getCondition();
            GraphNode node = pair.getNext();
            if (expectedCondition.equals(condition) && node == expectedNode) return;
        }
        fail(String.format("Could not find pair matching condition=%s, node=%s", expectedCondition, expectedNode));
    }

    private void mockRouterConditionRoutePairsWith(List<RouterConditionRoutePair> current) {
        ComponentData componentData = mock(ComponentData.class);
        doReturn(current).when(componentData).get("conditionRoutePairs");
        doReturn(componentData).when(routerNode1).componentData();
    }
}
package com.esb.plugin.component.type.router.functions;

import com.esb.plugin.AbstractGraphTest;
import com.esb.plugin.component.type.router.RouterConditionRoutePair;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        // When
        List<RouterConditionRoutePair> updatedPairs =
                SyncConditionAndRoutePairs.getUpdatedPairs(graph, routerNode1, current);

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

        // When
        List<RouterConditionRoutePair> updatedPairs =
                SyncConditionAndRoutePairs.getUpdatedPairs(graph, routerNode1, current);

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

        // When
        List<RouterConditionRoutePair> updatedPairs =
                SyncConditionAndRoutePairs.getUpdatedPairs(graph, routerNode1, current);

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

        // When
        List<RouterConditionRoutePair> updatedPairs =
                SyncConditionAndRoutePairs.getUpdatedPairs(graph, routerNode1, current);

        // Then
        assertThat(updatedPairs).hasSize(2);
        assertThatExistsPairWith(updatedPairs, "payload.name == 'John'", componentNode2);
        assertThatExistsPairWith(updatedPairs, "otherwise", componentNode1);
    }

    @Test
    void shouldThrowExceptionWhenRouterConditionPairsIsEmpty() {
        // Given
        FlowGraph graph = provider.createGraph();
        graph.root(root);
        graph.add(root, routerNode1);
        graph.add(routerNode1, componentNode1);

        List<RouterConditionRoutePair> current = new ArrayList<>();

        // When
        assertThrows(IllegalStateException.class,
                () -> SyncConditionAndRoutePairs.getUpdatedPairs(graph, routerNode1, current),
                "Expected numbers of condition pairs >= 1 but 0 were found");
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
                () -> SyncConditionAndRoutePairs.getUpdatedPairs(graph, routerNode1, current),
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

}
package com.reedelk.plugin.component.type.router.functions;

import com.reedelk.plugin.component.type.router.RouterConditionRoutePair;
import com.reedelk.plugin.component.type.router.RouterNode;
import com.reedelk.plugin.graph.FlowGraph;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.component.Router;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

public class SyncConditionAndRoutePairs {

    private static final String EMPTY_CONDITION = StringUtils.EMPTY;

    private SyncConditionAndRoutePairs() {
    }

    public static List<RouterConditionRoutePair> from(FlowGraph graph, RouterNode routerNode) {
        List<GraphNode> successors = graph.successors(routerNode);
        checkState(!successors.isEmpty(), format("Expected at least one successor for router node but %d were found", successors.size()));

        List<RouterConditionRoutePair> conditionAndRoutePairs = ListConditionRoutePairs.of(routerNode.componentData());
        List<RouterConditionRoutePair> copy = new ArrayList<>(conditionAndRoutePairs);

        // Just match up the successors
        conditionAndRoutePairs.clear();

        for (int i = 0; i < successors.size(); i++) {
            GraphNode node = successors.get(i);

            // The last one has always the default router condition.
            if (i == successors.size() - 1) {
                conditionAndRoutePairs.add(new RouterConditionRoutePair(Router.DEFAULT_CONDITION.value(), node));
                continue;
            }

            Optional<RouterConditionRoutePair> targetPair = findTargetPair(node, copy);
            if (targetPair.isPresent()) {
                conditionAndRoutePairs.add(targetPair.get());
            } else {
                conditionAndRoutePairs.add(new RouterConditionRoutePair(EMPTY_CONDITION, node));
            }
        }

        return conditionAndRoutePairs;

    }

    private static Optional<RouterConditionRoutePair> findTargetPair(GraphNode target, List<RouterConditionRoutePair> oldConditions) {
        return oldConditions.stream()
                .filter(routerConditionRoutePair ->
                        routerConditionRoutePair.getNext() == target &&
                                !Router.DEFAULT_CONDITION.value().equals(routerConditionRoutePair.getCondition()))
                .findFirst();
    }
}

package com.esb.plugin.component.type.router.functions;

import com.esb.plugin.component.type.router.RouterConditionRoutePair;
import com.esb.plugin.component.type.router.RouterNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.system.component.Router;

import java.util.*;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

public class SyncConditionAndRoutePairs {

    private static final String EMPTY_CONDITION = "";

    private SyncConditionAndRoutePairs() {
    }

    public static List<RouterConditionRoutePair> getUpdatedPairs(FlowGraph graph, RouterNode routerNode, List<RouterConditionRoutePair> oldConditions) {
        checkState(oldConditions.size() >= 1, format("Expected numbers of condition pairs >= 1 but %d were found", oldConditions.size()));

        List<GraphNode> successors = graph.successors(routerNode);
        checkState(successors.size() >= 1, format("Expected at least one successor for router node but %d were found", successors.size()));

        int numberOfSuccessors = successors.size();

        // Compute new router condition/s
        List<RouterConditionRoutePair> updatedConditions = new LinkedList<>();

        Collection<GraphNode> alreadyUsedNodes = new HashSet<>();

        for (int i = numberOfSuccessors - 1; i >= 0; i--) {
            GraphNode successor = successors.get(i);

            // The last successor is always otherwise.
            if (i == numberOfSuccessors - 1) {
                // Last element is always the default condition
                updatedConditions.add(new RouterConditionRoutePair(Router.DEFAULT_CONDITION, successor));
                continue;
            }

            // We look for an existing pair having this successor already paired with a condition
            Optional<RouterConditionRoutePair> targetPair = findTargetPair(successor, oldConditions);
            if (targetPair.isPresent()) {
                alreadyUsedNodes.add(targetPair.get().getNext());
                updatedConditions.add(new RouterConditionRoutePair(targetPair.get().getCondition(), successor));
                continue;
            }

            // We look for a condition not used yet at this successor's position
            Optional<RouterConditionRoutePair> oneAtIndexNotUsedYet = findOneAtIndexNotUsedYet(i, oldConditions, alreadyUsedNodes);
            if (oneAtIndexNotUsedYet.isPresent()) {
                alreadyUsedNodes.add(oneAtIndexNotUsedYet.get().getNext());
                updatedConditions.add(new RouterConditionRoutePair(oneAtIndexNotUsedYet.get().getCondition(), successor));
                continue;
            }

            // Otherwise we create a new empty condition -> successor pair
            updatedConditions.add(new RouterConditionRoutePair(EMPTY_CONDITION, successor));
        }

        Collections.reverse(updatedConditions);
        return updatedConditions;

    }

    private static Optional<RouterConditionRoutePair> findTargetPair(GraphNode target, List<RouterConditionRoutePair> oldConditions) {
        return oldConditions
                .stream()
                .filter(routerConditionRoutePair ->
                        routerConditionRoutePair.getNext() == target &&
                                !routerConditionRoutePair.getCondition().equals(Router.DEFAULT_CONDITION))
                .findFirst();
    }

    private static Optional<RouterConditionRoutePair> findOneAtIndexNotUsedYet(int i, List<RouterConditionRoutePair> routerConditionRoutePairs, Collection<GraphNode> alreadyUsedNodes) {
        if (i < routerConditionRoutePairs.size()) {
            RouterConditionRoutePair pair = routerConditionRoutePairs.get(i);
            if (!alreadyUsedNodes.contains(pair.getNext()) &&
                    !pair.getCondition().equals(Router.DEFAULT_CONDITION)) {
                return Optional.of(pair);
            }
        }
        return Optional.empty();
    }
}

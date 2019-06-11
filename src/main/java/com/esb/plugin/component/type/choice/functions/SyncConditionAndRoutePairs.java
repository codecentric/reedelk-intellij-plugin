package com.esb.plugin.component.type.choice.functions;

import com.esb.plugin.component.type.choice.ChoiceConditionRoutePair;
import com.esb.plugin.component.type.choice.ChoiceNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.system.component.Choice;

import java.util.*;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;

public class SyncConditionAndRoutePairs {

    private static final String EMPTY_CONDITION = "";

    public static List<ChoiceConditionRoutePair> getUpdatedPairs(FlowGraph graph, ChoiceNode choiceNode, List<ChoiceConditionRoutePair> oldConditions) {
        checkState(oldConditions.size() >= 1, format("Expected numbers of condition pairs >= 1 but %d were found", oldConditions.size()));

        List<GraphNode> successors = graph.successors(choiceNode);
        checkState(successors.size() >= 1, format("Expected at least one successor for choice node but %d were found", successors.size()));

        int numberOfSuccessors = successors.size();

        // Compute new Choice condition
        List<ChoiceConditionRoutePair> updatedConditions = new LinkedList<>();

        Collection<GraphNode> alreadyUsedNodes = new HashSet<>();

        for (int i = numberOfSuccessors - 1; i >= 0; i--) {
            GraphNode successor = successors.get(i);

            // The last successor is always otherwise.
            if (i == numberOfSuccessors - 1) {
                // Last element is always the default condition
                updatedConditions.add(new ChoiceConditionRoutePair(Choice.DEFAULT_CONDITION, successor));
                continue;
            }

            // We look for an existing pair having this successor already paired with a condition
            Optional<ChoiceConditionRoutePair> targetPair = findTargetPair(successor, oldConditions);
            if (targetPair.isPresent()) {
                alreadyUsedNodes.add(targetPair.get().getNext());
                updatedConditions.add(new ChoiceConditionRoutePair(targetPair.get().getCondition(), successor));
                continue;
            }

            // We look for a condition not used yet at this successor's position
            Optional<ChoiceConditionRoutePair> oneAtIndexNotUsedYet = findOneAtIndexNotUsedYet(i, oldConditions, alreadyUsedNodes);
            if (oneAtIndexNotUsedYet.isPresent()) {
                alreadyUsedNodes.add(oneAtIndexNotUsedYet.get().getNext());
                updatedConditions.add(new ChoiceConditionRoutePair(oneAtIndexNotUsedYet.get().getCondition(), successor));
                continue;
            }

            // Otherwise we create a new empty condition -> successor pair
            updatedConditions.add(new ChoiceConditionRoutePair(EMPTY_CONDITION, successor));
        }

        Collections.reverse(updatedConditions);
        return updatedConditions;

    }

    private static Optional<ChoiceConditionRoutePair> findTargetPair(GraphNode target, List<ChoiceConditionRoutePair> oldConditions) {
        return oldConditions
                .stream()
                .filter(choiceConditionRoutePair ->
                        choiceConditionRoutePair.getNext() == target &&
                                !choiceConditionRoutePair.getCondition().equals(Choice.DEFAULT_CONDITION))
                .findFirst();
    }

    private static Optional<ChoiceConditionRoutePair> findOneAtIndexNotUsedYet(int i, List<ChoiceConditionRoutePair> choiceConditionRoutePairs, Collection<GraphNode> alreadyUsedNodes) {
        if (i < choiceConditionRoutePairs.size()) {
            ChoiceConditionRoutePair pair = choiceConditionRoutePairs.get(i);
            if (!alreadyUsedNodes.contains(pair.getNext()) &&
                    !pair.getCondition().equals(Choice.DEFAULT_CONDITION)) {
                return Optional.of(pair);
            }
        }
        return Optional.empty();
    }
}

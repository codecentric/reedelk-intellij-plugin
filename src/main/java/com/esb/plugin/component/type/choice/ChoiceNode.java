package com.esb.plugin.component.type.choice;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.designer.drawables.AbstractScopedGraphNode;
import com.esb.plugin.graph.node.GraphNode;

import java.util.LinkedList;
import java.util.List;

public class ChoiceNode extends AbstractScopedGraphNode {

    public static final String DEFAULT_CONDITION_NAME = "otherwise";
    public static final String DATA_CONDITION_ROUTE_PAIRS = "conditionRoutePairs";

    private static final String EMPTY_CONDITION = "";

    public ChoiceNode(ComponentData componentData) {
        super(componentData);
    }

    @Override
    public void onSuccessorAdded(GraphNode successor) {
        executeIfRouteToNodeNotPresent(successor, conditionRoutePairs ->
                conditionRoutePairs.add(new ChoiceConditionRoutePair(EMPTY_CONDITION, successor)));
    }

    @Override
    public void onSuccessorAdded(GraphNode successor, int index) {
        executeIfRouteToNodeNotPresent(successor, conditionRoutePairs ->
                conditionRoutePairs.add(index, new ChoiceConditionRoutePair(EMPTY_CONDITION, successor)));
    }

    @Override
    public void onSuccessorRemoved(GraphNode successor) {
        listConditionRoutePairs()
                .removeIf(choiceConditionRoutePair ->
                        choiceConditionRoutePair.getNext() == successor);
    }

    interface Action {
        void execute(List<ChoiceConditionRoutePair> conditionRoutePairs);
    }

    private void executeIfRouteToNodeNotPresent(GraphNode target, Action action) {
        List<ChoiceConditionRoutePair> conditionRoutePairList = listConditionRoutePairs();
        boolean isPresent = conditionRoutePairList.stream()
                .anyMatch(choiceConditionRoutePair ->
                        choiceConditionRoutePair.getNext() == target);
        if (!isPresent) {
            action.execute(conditionRoutePairList);
        }
    }

    private List<ChoiceConditionRoutePair> listConditionRoutePairs() {
        ComponentData component = componentData();
        // TODO: this cast should be fixed
        List<ChoiceConditionRoutePair> conditionRoutePair =
                (List<ChoiceConditionRoutePair>) component.get(DATA_CONDITION_ROUTE_PAIRS);
        if (conditionRoutePair == null) {
            conditionRoutePair = new LinkedList<>();
            component.set(DATA_CONDITION_ROUTE_PAIRS, conditionRoutePair);
        }
        return conditionRoutePair;
    }
}

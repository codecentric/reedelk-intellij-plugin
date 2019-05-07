package com.esb.plugin.component.choice;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.designer.canvas.drawables.AbstractScopedGraphNode;
import com.esb.plugin.graph.node.GraphNode;

import java.util.LinkedList;
import java.util.List;

public class ChoiceNode extends AbstractScopedGraphNode {

    public static final String DATA_CONDITION_ROUTE_PAIRS = "conditionRoutePairs";

    private static final String EMPTY_CONDITION = "";

    public ChoiceNode(ComponentData componentData) {
        super(componentData);
    }

    @Override
    public void onSuccessorAdded(GraphNode successor) {
        List<ChoiceConditionRoutePair> conditionRoutePairList = listConditionRoutePairs();
        boolean isAlreadyPresent = conditionRoutePairList
                .stream()
                .anyMatch(choiceConditionRoutePair -> choiceConditionRoutePair.getNext() == successor);
        if (!isAlreadyPresent) {
            conditionRoutePairList.add(new ChoiceConditionRoutePair(EMPTY_CONDITION, successor));
        }
    }

    @Override
    public void onSuccessorRemoved(GraphNode successor) {
        listConditionRoutePairs()
                .removeIf(choiceConditionRoutePair ->
                        choiceConditionRoutePair.getNext() == successor);
    }

    private List<ChoiceConditionRoutePair> listConditionRoutePairs() {
        ComponentData component = component();
        List<ChoiceConditionRoutePair> conditionRoutePair = (List<ChoiceConditionRoutePair>) component.get(DATA_CONDITION_ROUTE_PAIRS);
        if (conditionRoutePair == null) {
            conditionRoutePair = new LinkedList<>();
            component.set(DATA_CONDITION_ROUTE_PAIRS, conditionRoutePair);
        }
        return conditionRoutePair;
    }
}

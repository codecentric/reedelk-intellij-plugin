package com.reedelk.plugin.component.type.router.functions;

import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.runtime.component.Router;

public class IsDefaultRoute {

    public static boolean of(GraphNode source, GraphNode target) {
        return ListConditionRoutePairs.of(source.componentData())
                .stream()
                .anyMatch(routerConditionRoutePair ->
                        Router.DEFAULT_CONDITION.equals(routerConditionRoutePair.getCondition()) &&
                                routerConditionRoutePair.getNext() == target);
    }
}

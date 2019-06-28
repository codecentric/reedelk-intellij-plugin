package com.esb.plugin.component.type.router.functions;

import com.esb.plugin.graph.node.GraphNode;
import com.esb.system.component.Router;

public class IsDefaultRoute {

    public static boolean of(GraphNode source, GraphNode target) {
        return ListConditionRoutePairs.of(source.componentData())
                .stream()
                .anyMatch(routerConditionRoutePair ->
                        Router.DEFAULT_CONDITION.equals(routerConditionRoutePair.getCondition()) &&
                                routerConditionRoutePair.getNext() == target);
    }
}

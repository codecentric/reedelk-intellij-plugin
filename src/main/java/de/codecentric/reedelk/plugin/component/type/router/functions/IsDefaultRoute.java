package de.codecentric.reedelk.plugin.component.type.router.functions;

import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.runtime.component.Router;

public class IsDefaultRoute {

    private IsDefaultRoute() {
    }

    public static boolean of(GraphNode source, GraphNode target) {
        return ListConditionRoutePairs.of(source.componentData())
                .stream()
                .anyMatch(routerConditionRoutePair ->
                        Router.DEFAULT_CONDITION.value().equals(routerConditionRoutePair.getCondition()) &&
                                routerConditionRoutePair.getNext() == target);
    }
}

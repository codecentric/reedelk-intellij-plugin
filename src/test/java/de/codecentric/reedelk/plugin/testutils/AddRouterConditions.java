package de.codecentric.reedelk.plugin.testutils;

import de.codecentric.reedelk.plugin.component.type.router.RouterConditionRoutePair;
import de.codecentric.reedelk.plugin.component.type.router.RouterNode;
import de.codecentric.reedelk.plugin.component.type.router.functions.ListConditionRoutePairs;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;

import java.util.List;

public class AddRouterConditions {

    public static void addConditionRoutePairs(RouterNode routerNode, String routeCondition, GraphNode routeNode) {
        List<RouterConditionRoutePair> pairs = ListConditionRoutePairs.of(routerNode.componentData());
        pairs.add(new RouterConditionRoutePair(routeCondition, routeNode));
    }

    public static void addConditionRoutePairs(RouterNode routerNode, String routeCondition1, GraphNode routeNode1, String routeCondition2, GraphNode routeNode2) {
        List<RouterConditionRoutePair> pairs = ListConditionRoutePairs.of(routerNode.componentData());
        pairs.add(new RouterConditionRoutePair(routeCondition1, routeNode1));
        pairs.add(new RouterConditionRoutePair(routeCondition2, routeNode2));
    }
}

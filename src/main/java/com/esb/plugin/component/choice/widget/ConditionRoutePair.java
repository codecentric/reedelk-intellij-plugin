package com.esb.plugin.component.choice.widget;

import com.esb.plugin.graph.node.GraphNode;

class ConditionRoutePair {

    String condition;
    GraphNode node;

    ConditionRoutePair(String condition, GraphNode node) {
        this.condition = condition;
        this.node = node;
    }

}

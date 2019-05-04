package com.esb.plugin.component.choice;

import com.esb.plugin.graph.node.GraphNode;

public class ChoiceConditionRoutePair {

    private String condition;
    private GraphNode next;

    public ChoiceConditionRoutePair(String condition, GraphNode next) {
        this.condition = condition;
        this.next = next;
    }

    public String getCondition() {
        return condition;
    }

    public GraphNode getNext() {
        return next;
    }

    public void setNext(GraphNode next) {
        this.next = next;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}

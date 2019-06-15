package com.esb.plugin.component.type.router;

import com.esb.plugin.graph.node.GraphNode;

public class RouterConditionRoutePair {

    private String condition;
    private GraphNode next;

    public RouterConditionRoutePair(String condition, GraphNode next) {
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

    @Override
    public String toString() {
        return "RouterConditionRoutePair{" +
                "condition='" + condition + '\'' +
                ", next=" + next +
                '}';
    }
}

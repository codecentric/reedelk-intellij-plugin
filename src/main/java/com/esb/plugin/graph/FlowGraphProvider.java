package com.esb.plugin.graph;

public class FlowGraphProvider {
    public FlowGraph createGraph(String id) {
        return new FlowGraphImpl(id);
    }
}

package com.reedelk.plugin.graph;

public class FlowGraphProvider {
    public FlowGraph createGraph(String id) {
        return new FlowGraphImpl(id);
    }
}

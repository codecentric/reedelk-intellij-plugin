package com.esb.plugin.graph;

import java.util.UUID;

public class FlowGraphProvider {

    public FlowGraph createGraph(String id) {
        return new FlowGraphImpl(id);
    }

    public FlowGraph createGraph() {
        String id = UUID.randomUUID().toString();
        return new FlowGraphImpl(id);
    }

}

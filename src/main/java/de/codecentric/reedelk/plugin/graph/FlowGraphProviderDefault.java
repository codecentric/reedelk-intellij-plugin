package de.codecentric.reedelk.plugin.graph;

public class FlowGraphProviderDefault implements FlowGraphProvider {

    @Override
    public FlowGraph createGraph(String id) {
        return new FlowGraphImpl(id);
    }
}

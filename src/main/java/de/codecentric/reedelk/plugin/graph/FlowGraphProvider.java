package de.codecentric.reedelk.plugin.graph;

public interface FlowGraphProvider {

    static FlowGraphProvider get() {
        return new FlowGraphProviderDefault();
    }

    FlowGraph createGraph(String id);
}

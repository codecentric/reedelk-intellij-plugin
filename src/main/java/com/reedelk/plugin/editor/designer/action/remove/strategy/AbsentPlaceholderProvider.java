package com.reedelk.plugin.editor.designer.action.remove.strategy;

import com.reedelk.plugin.graph.node.GraphNode;

import java.util.Optional;

public class AbsentPlaceholderProvider implements PlaceholderProvider {

    @Override
    public Optional<GraphNode> get() {
        return Optional.empty();
    }

    @Override
    public Optional<GraphNode> get(String description) {
        return Optional.empty();
    }
}

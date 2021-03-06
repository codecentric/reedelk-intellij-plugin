package de.codecentric.reedelk.plugin.editor.designer.action.remove.strategy;

import de.codecentric.reedelk.plugin.graph.node.GraphNode;

import java.util.Optional;

public interface PlaceholderProvider {

    Optional<GraphNode> get();

    Optional<GraphNode> get(String description);

}

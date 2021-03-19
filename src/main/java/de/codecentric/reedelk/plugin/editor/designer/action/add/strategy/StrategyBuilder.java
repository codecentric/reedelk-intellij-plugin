package de.codecentric.reedelk.plugin.editor.designer.action.add.strategy;

import de.codecentric.reedelk.plugin.component.type.placeholder.PlaceholderNode;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.plugin.graph.node.ScopedGraphNode;
import de.codecentric.reedelk.plugin.commons.IsScopedGraphNode;
import de.codecentric.reedelk.plugin.editor.designer.action.ActionStrategy;
import de.codecentric.reedelk.plugin.editor.designer.action.remove.strategy.PlaceholderProvider;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkState;

public abstract class StrategyBuilder {

    protected Point dropPoint;
    protected FlowGraph graph;
    protected Graphics2D graphics;
    protected ImageObserver observer;
    protected PlaceholderProvider placeholderProvider;

    public StrategyBuilder graph(FlowGraph graph) {
        this.graph = graph;
        return this;
    }

    public StrategyBuilder dropPoint(Point dropPoint) {
        this.dropPoint = dropPoint;
        return this;
    }

    public StrategyBuilder graphics(Graphics2D graphics) {
        this.graphics = graphics;
        return this;
    }

    public StrategyBuilder observer(ImageObserver observer) {
        this.observer = observer;
        return this;
    }

    public StrategyBuilder placeholderProvider(PlaceholderProvider placeholderProvider) {
        this.placeholderProvider = placeholderProvider;
        return this;
    }

    @NotNull
    public abstract ActionStrategy build();

    @NotNull
    ActionStrategy strategyFromClosestPrecedingNode(GraphNode precedingNode) {
        if (graph.successors(precedingNode).isEmpty()) {
            return new PrecedingNodeWithoutSuccessorAction(graph, dropPoint, precedingNode, graphics, placeholderProvider);

        } else if (IsScopedGraphNode.of(precedingNode)) {
            ScopedGraphNode scopedGraphNode = (ScopedGraphNode) precedingNode;
            if (hasOnlyOneSuccessorOutsideScope(scopedGraphNode, graph)) {
                return new PrecedingNodeWithOneSuccessorAction(graph, dropPoint, scopedGraphNode, graphics, placeholderProvider);
            } else {
                return new PrecedingScopedNodeAction(graph, dropPoint, scopedGraphNode, graphics, placeholderProvider);
            }

        } else {
            // Only ScopedGraphNode nodes might have multiple successors. In all other cases
            // a node in the flow must have at most one successor.
            checkState(graph.successors(precedingNode).size() == 1,
                    "Successors size MUST be 1, otherwise it must be a Scoped Node");
            return new PrecedingNodeWithOneSuccessorAction(graph, dropPoint, precedingNode, graphics, placeholderProvider);
        }
    }

    @NotNull
    GraphNode getOverlappingPlaceholder(FlowGraph graph, Point dropPoint) {
        Optional<GraphNode> maybeOverlappingPlaceholder = graph.nodes()
                .stream()
                .filter(node -> node.contains(observer, dropPoint.x, dropPoint.y) &&
                        node instanceof PlaceholderNode)
                .findFirst();
        if (!maybeOverlappingPlaceholder.isPresent()) {
            throw new IllegalStateException("Expected at least one placeholder matching the drop point");
        }
        return maybeOverlappingPlaceholder.get();
    }

    boolean isOverlappingAnyPlaceHolder(FlowGraph graph, Point dropPoint) {
        return graph.nodes()
                .stream()
                .anyMatch(node -> node.contains(observer, dropPoint.x, dropPoint.y) &&
                        node instanceof PlaceholderNode);
    }

    private boolean hasOnlyOneSuccessorOutsideScope(ScopedGraphNode closestPrecedingNode, FlowGraph graph) {
        List<GraphNode> successors = graph.successors(closestPrecedingNode);
        return successors.size() == 1 && !closestPrecedingNode.scopeContains(successors.get(0));
    }
}

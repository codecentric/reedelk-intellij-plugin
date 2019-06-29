package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.component.type.placeholder.PlaceholderNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

public abstract class StrategyBuilder {

    protected ImageObserver observer;
    protected Graphics2D graphics;
    protected FlowGraph graph;
    protected Point dropPoint;

    public StrategyBuilder observer(ImageObserver observer) {
        this.observer = observer;
        return this;
    }

    public StrategyBuilder graphics(Graphics2D graphics) {
        this.graphics = graphics;
        return this;
    }

    public StrategyBuilder graph(FlowGraph graph) {
        this.graph = graph;
        return this;
    }

    public StrategyBuilder dropPoint(Point dropPoint) {
        this.dropPoint = dropPoint;
        return this;
    }

    @NotNull
    public abstract Strategy build();


    @NotNull
    Strategy getStrategyForClosestPrecedingNode(GraphNode precedingNode) {
        if (graph.successors(precedingNode).isEmpty()) {
            return new PrecedingNodeWithoutSuccessor(graph, dropPoint, precedingNode, graphics);

        } else if (precedingNode instanceof ScopedGraphNode) {
            ScopedGraphNode scopedGraphNode = (ScopedGraphNode) precedingNode;
            if (hasOnlyOneSuccessorOutsideScope(scopedGraphNode, graph)) {
                return new PrecedingNodeWithOneSuccessor(graph, dropPoint, scopedGraphNode, graphics);
            } else {
                return new PrecedingScopedNode(graph, dropPoint, scopedGraphNode, graphics);
            }

        } else {
            // Only ScopedGraphNode nodes might have multiple successors. In all other cases
            // a node in the flow must have at most one successor.
            checkState(graph.successors(precedingNode).size() == 1,
                    "Successors size MUST be 1, otherwise it must be a Scoped Node");
            return new PrecedingNodeWithOneSuccessor(graph, dropPoint, precedingNode, graphics);
        }
    }

    boolean isOverlappingAnyPlaceHolder(FlowGraph graph, Point dropPoint) {
        return graph.nodes()
                .stream()
                .anyMatch(node -> node.contains(observer, dropPoint.x, dropPoint.y) &&
                        node instanceof PlaceholderNode);
    }

    @NotNull
    GraphNode getOverlappingPlaceholder(FlowGraph graph, Point dropPoint) {
        return graph.nodes()
                .stream()
                .filter(node -> node.contains(observer, dropPoint.x, dropPoint.y) &&
                        node instanceof PlaceholderNode)
                .findFirst()
                .get();
    }

    private boolean hasOnlyOneSuccessorOutsideScope(ScopedGraphNode closestPrecedingNode, FlowGraph graph) {
        List<GraphNode> successors = graph.successors(closestPrecedingNode);
        return successors.size() == 1 && !closestPrecedingNode.scopeContains(successors.get(0));
    }

}

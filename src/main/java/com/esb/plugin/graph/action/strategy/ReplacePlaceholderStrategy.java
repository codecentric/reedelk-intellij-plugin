package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.utils.FindScope;

import java.awt.*;
import java.util.List;

// TODO: Must take in consideration that the node might be outside the scope...
public class ReplacePlaceholderStrategy extends AbstractStrategy {

    public ReplacePlaceholderStrategy(FlowGraph graph, Point dropPoint, Connector connector, Graphics2D graphics) {
        super(graph, dropPoint, connector, graphics);
    }

    @Override
    public void execute(GraphNode placeHolder) {

        // Connect predecessors
        List<GraphNode> predecessorsOfPlaceHolder = graph.predecessors(placeHolder);
        predecessorsOfPlaceHolder.forEach(connector::addPredecessor);

        // Connect successors
        List<GraphNode> successorsOfPlaceHolder = graph.successors(placeHolder);
        successorsOfPlaceHolder.forEach(connector::addSuccessor);

        // Add to scope the new node replacing the placeholder
        // Remove from scope the placeholder
        FindScope.of(graph, placeHolder).ifPresent(scopedGraphNode -> {
            connector.addToScope(scopedGraphNode);
            scopedGraphNode.removeFromScope(placeHolder);
        });

        // Remove the placeholder node from the graph (including inbound/outbound edges)
        graph.remove(placeHolder);

    }
}

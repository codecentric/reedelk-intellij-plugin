package com.esb.plugin.graph.action.strategy;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.ScopedGraphNode;
import com.esb.plugin.graph.utils.FindScope;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

// TODO: Must take in consideration that the node might be outside the scope...
public class ReplacePlaceholderStrategy extends AbstractStrategy {

    public ReplacePlaceholderStrategy(FlowGraph graph, Point dropPoint, Connector connector, Graphics2D graphics) {
        super(graph, dropPoint, connector, graphics);
    }

    @Override
    public void execute(GraphNode placeHolder) {
        List<GraphNode> predecessorsOfPlaceHolder = graph.predecessors(placeHolder);
        List<GraphNode> successorsOfPlaceHolder = graph.successors(placeHolder);
        connector.add();
        predecessorsOfPlaceHolder.forEach(connector::addPredecessor);
        successorsOfPlaceHolder.forEach(connector::addSuccessor);
        FindScope.of(graph, placeHolder).ifPresent(new Consumer<ScopedGraphNode>() {
            @Override
            public void accept(ScopedGraphNode scopedGraphNode) {
                connector.addToScope(scopedGraphNode);
                scopedGraphNode.removeFromScope(placeHolder);
            }
        });
        graph.remove(placeHolder);

    }
}

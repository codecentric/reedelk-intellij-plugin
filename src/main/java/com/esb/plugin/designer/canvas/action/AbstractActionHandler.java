package com.esb.plugin.designer.canvas.action;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.action.AddNodeAction;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.connector.ConnectorFactory;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;

import java.awt.*;

abstract class AbstractActionHandler {

    protected final Module module;

    AbstractActionHandler(final Module module) {
        this.module = module;
    }

    FlowGraphChangeAware addNodeToGraph(FlowGraph graph, GraphNode dropped, Point dropPoint, Graphics2D graphics) {

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);

        Connector connector = ConnectorFactory.get()
                .componentToAdd(dropped)
                .graph(modifiableGraph)
                .module(module)
                .build();

        AddNodeAction addNodeAction = new AddNodeAction(modifiableGraph, dropPoint, connector, graphics);
        addNodeAction.execute();

        return modifiableGraph;
    }

}

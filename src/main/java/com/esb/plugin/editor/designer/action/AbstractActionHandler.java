package com.esb.plugin.editor.designer.action;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.action.ActionNodeAdd;
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

    void addNodeToGraph(FlowGraph graph, GraphNode dropped, Point dropPoint, Graphics2D graphics) {

        Connector connector = ConnectorFactory.get()
                .nodeToAdd(dropped)
                .graph(graph)
                .module(module)
                .build();

        ActionNodeAdd actionNodeAdd = new ActionNodeAdd(graph, dropPoint, connector, graphics);
        actionNodeAdd.execute();
    }

}

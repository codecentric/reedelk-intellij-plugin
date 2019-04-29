package com.esb.plugin.designer.editor.component;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphChangeAware;
import com.esb.plugin.designer.graph.GraphNode;
import com.esb.plugin.designer.graph.action.AddDrawableToGraph;
import com.esb.plugin.designer.graph.connector.Connector;
import com.esb.plugin.designer.graph.connector.ConnectorFactory;
import com.intellij.openapi.module.Module;

import java.awt.*;

abstract class AbstractActionHandler {

    private final Module module;

    AbstractActionHandler(final Module module) {
        this.module = module;
    }

    FlowGraphChangeAware addDrawableToGraph(FlowGraph graph, GraphNode dropped, Point dropPoint, Graphics2D graphics) {

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);

        Connector connector = ConnectorFactory.get()
                .componentToAdd(dropped)
                .graph(modifiableGraph)
                .module(module)
                .build();

        AddDrawableToGraph componentAdder = new AddDrawableToGraph(modifiableGraph, dropPoint, connector, graphics);
        componentAdder.add();

        return modifiableGraph;
    }


}

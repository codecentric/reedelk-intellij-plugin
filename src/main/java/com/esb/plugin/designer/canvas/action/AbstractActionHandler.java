package com.esb.plugin.designer.canvas.action;

import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphChangeAware;
import com.esb.plugin.graph.action.AddNodeAction;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.connector.ConnectorFactory;
import com.esb.plugin.graph.manager.GraphChangeNotifier;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;

import java.awt.*;

abstract class AbstractActionHandler {

    protected final Module module;
    protected final VirtualFile relatedFile;

    AbstractActionHandler(final Module module, VirtualFile relatedFile) {
        this.module = module;
        this.relatedFile = relatedFile;
    }

    FlowGraphChangeAware addDrawableToGraph(FlowGraph graph, GraphNode dropped, Point dropPoint, Graphics2D graphics) {

        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);

        Connector connector = ConnectorFactory.get()
                .componentToAdd(dropped)
                .graph(modifiableGraph)
                .module(module)
                .build();

        AddNodeAction componentAdder = new AddNodeAction(modifiableGraph, dropPoint, connector, graphics);
        componentAdder.add();

        return modifiableGraph;
    }

    protected void notifyGraphChanged(FlowGraph graph) {
        GraphChangeNotifier notifier = module.getMessageBus().syncPublisher(GraphChangeNotifier.TOPIC);
        notifier.onChange(graph, relatedFile);
    }

}

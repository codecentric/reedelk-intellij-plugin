package com.esb.plugin.component.forkjoin;

import com.esb.component.FlowReference;
import com.esb.plugin.component.flowreference.FlowReferenceGraphNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.connector.ConnectorBuilder;
import com.esb.plugin.graph.connector.ScopeNodeConnector;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.GraphNodeFactory;
import com.intellij.openapi.module.Module;

public class ForkJoinDrawableConnectorBuilder implements ConnectorBuilder {

    @Override
    public Connector build(Module module, FlowGraph graph, GraphNode componentToAdd) {

        FlowGraph forkJoinGraph = new FlowGraphImpl();
        forkJoinGraph.root(componentToAdd);

        FlowReferenceGraphNode placeholderDrawable = GraphNodeFactory.get(module, FlowReference.class.getName());
        forkJoinGraph.add(componentToAdd, placeholderDrawable);

        ((ForkJoinGraphNode) componentToAdd).addToScope(placeholderDrawable);

        return new ScopeNodeConnector(graph, forkJoinGraph);
    }
}

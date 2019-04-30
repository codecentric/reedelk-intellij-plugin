package com.esb.plugin.component.choice;

import com.esb.component.FlowReference;
import com.esb.plugin.component.flowreference.FlowReferenceNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.connector.ConnectorBuilder;
import com.esb.plugin.graph.connector.ScopedNodeConnector;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.GraphNodeFactory;
import com.intellij.openapi.module.Module;

public class ChoiceConnectorBuilder implements ConnectorBuilder {

    @Override
    public Connector build(Module module, FlowGraph graph, GraphNode componentToAdd) {
        FlowGraph choiceGraph = new FlowGraphImpl();
        choiceGraph.root(componentToAdd);

        FlowReferenceNode placeholder = GraphNodeFactory.get(module, FlowReference.class.getName());

        choiceGraph.add(componentToAdd, placeholder);

        ((ChoiceNode) componentToAdd).addToScope(placeholder);

        return new ScopedNodeConnector(graph, choiceGraph);
    }
}

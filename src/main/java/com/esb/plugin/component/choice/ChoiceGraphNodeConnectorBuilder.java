package com.esb.plugin.component.choice;

import com.esb.component.FlowReference;
import com.esb.plugin.component.Component;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.flowreference.FlowReferenceGraphNode;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.connector.ConnectorBuilder;
import com.esb.plugin.graph.connector.ScopeNodeConnector;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.service.module.ComponentService;
import com.intellij.openapi.module.Module;

public class ChoiceGraphNodeConnectorBuilder implements ConnectorBuilder {

    @Override
    public Connector build(Module module, FlowGraph graph, GraphNode componentToAdd) {
        FlowGraph choiceGraph = new FlowGraphImpl();
        choiceGraph.root(componentToAdd);

        ComponentDescriptor componentDescriptor = ComponentService.getInstance(module)
                .componentDescriptorByName(FlowReference.class.getName());
        Component component = new Component(componentDescriptor);

        FlowReferenceGraphNode placeholderDrawable = new FlowReferenceGraphNode(component);
        choiceGraph.add(componentToAdd, placeholderDrawable);

        ((ChoiceGraphNode) componentToAdd).addToScope(placeholderDrawable);

        return new ScopeNodeConnector(graph, choiceGraph);
    }
}

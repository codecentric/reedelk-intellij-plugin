package com.esb.plugin.component.forkjoin;

import com.esb.component.FlowReference;
import com.esb.plugin.component.Component;
import com.esb.plugin.component.ComponentDescriptor;
import com.esb.plugin.component.flowreference.FlowReferenceDrawable;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowGraphImpl;
import com.esb.plugin.graph.GraphNode;
import com.esb.plugin.graph.connector.Connector;
import com.esb.plugin.graph.connector.ConnectorBuilder;
import com.esb.plugin.graph.connector.ScopeDrawableConnector;
import com.esb.plugin.service.module.ComponentService;
import com.intellij.openapi.module.Module;

public class ForkJoinDrawableConnectorBuilder implements ConnectorBuilder {

    @Override
    public Connector build(Module module, FlowGraph graph, GraphNode componentToAdd) {

        FlowGraph forkJoinGraph = new FlowGraphImpl();
        forkJoinGraph.root(componentToAdd);

        ComponentDescriptor descriptor = ComponentService.getInstance(module)
                .componentDescriptorByName(FlowReference.class.getName());
        Component component = new Component(descriptor);

        FlowReferenceDrawable placeholderDrawable = new FlowReferenceDrawable(component);
        forkJoinGraph.add(componentToAdd, placeholderDrawable);

        ((ForkJoinDrawable) componentToAdd).addToScope(placeholderDrawable);

        return new ScopeDrawableConnector(graph, forkJoinGraph);
    }
}

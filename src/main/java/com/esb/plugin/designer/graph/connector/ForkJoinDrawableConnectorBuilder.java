package com.esb.plugin.designer.graph.connector;

import com.esb.component.FlowReference;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.GraphNode;
import com.esb.plugin.designer.graph.drawable.FlowReferenceDrawable;
import com.esb.plugin.designer.graph.drawable.ForkJoinDrawable;
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

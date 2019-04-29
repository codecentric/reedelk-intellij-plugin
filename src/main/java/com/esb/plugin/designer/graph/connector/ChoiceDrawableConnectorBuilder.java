package com.esb.plugin.designer.graph.connector;

import com.esb.component.FlowReference;
import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.GraphNode;
import com.esb.plugin.designer.graph.drawable.ChoiceDrawable;
import com.esb.plugin.designer.graph.drawable.FlowReferenceDrawable;
import com.esb.plugin.service.module.ComponentService;
import com.intellij.openapi.module.Module;

public class ChoiceDrawableConnectorBuilder implements ConnectorBuilder {

    @Override
    public Connector build(Module module, FlowGraph graph, GraphNode componentToAdd) {
        FlowGraph choiceGraph = new FlowGraphImpl();
        choiceGraph.root(componentToAdd);

        ComponentDescriptor componentDescriptor = ComponentService.getInstance(module)
                .componentDescriptorByName(FlowReference.class.getName());
        Component component = new Component(componentDescriptor);

        FlowReferenceDrawable placeholderDrawable = new FlowReferenceDrawable(component);
        choiceGraph.add(componentToAdd, placeholderDrawable);

        ((ChoiceDrawable) componentToAdd).addToScope(placeholderDrawable);

        return new ScopeDrawableConnector(graph, choiceGraph);
    }
}

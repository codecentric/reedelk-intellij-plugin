package com.esb.plugin.designer.editor.designer;

import com.esb.component.FlowReference;
import com.esb.plugin.designer.editor.component.ComponentDescriptor;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphChangeAware;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.action.AddDrawableToGraph;
import com.esb.plugin.designer.graph.connector.Connector;
import com.esb.plugin.designer.graph.connector.DrawableConnector;
import com.esb.plugin.designer.graph.connector.ScopeDrawableConnector;
import com.esb.plugin.designer.graph.drawable.ChoiceDrawable;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.FlowReferenceDrawable;
import com.esb.plugin.designer.graph.drawable.ForkJoinDrawable;
import com.esb.plugin.service.module.ComponentService;
import com.intellij.openapi.module.Module;

import java.awt.*;

abstract class AbstractActionHandler {

    private final Module module;

    AbstractActionHandler(Module module) {
        this.module = module;
    }

    // TODO: Here you should provide a proper constructor for connector.
    protected FlowGraphChangeAware addDrawableToGraph(FlowGraph graph, Drawable dropped, Point dropPoint, Graphics2D graphics) {
        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);
        Connector connector = createComponentConnector(dropped, modifiableGraph);
        AddDrawableToGraph componentAdder = new AddDrawableToGraph(modifiableGraph, dropPoint, connector, graphics);
        componentAdder.add();
        return modifiableGraph;
    }

    protected Connector createComponentConnector(Drawable componentToAdd, FlowGraph graph) {
        // TODO: fix this
        if (componentToAdd instanceof ChoiceDrawable) {
            FlowGraph choiceGraph = new FlowGraphImpl();
            choiceGraph.root(componentToAdd);

            ComponentDescriptor flowReference = ComponentService.getInstance(module).componentDescriptorByName(FlowReference.class.getName());
            FlowReferenceDrawable placeholderDrawable = new FlowReferenceDrawable(flowReference);
            choiceGraph.add(componentToAdd, placeholderDrawable);
            ((ChoiceDrawable) componentToAdd).addToScope(placeholderDrawable);
            return new ScopeDrawableConnector(graph, choiceGraph);
        }
        if (componentToAdd instanceof ForkJoinDrawable) {
            FlowGraph forkJoinGraph = new FlowGraphImpl();
            forkJoinGraph.root(componentToAdd);

            ComponentDescriptor flowReference = ComponentService.getInstance(module).componentDescriptorByName(FlowReference.class.getName());
            FlowReferenceDrawable placeholderDrawable = new FlowReferenceDrawable(flowReference);
            forkJoinGraph.add(componentToAdd, placeholderDrawable);
            ((ForkJoinDrawable) componentToAdd).addToScope(placeholderDrawable);
            return new ScopeDrawableConnector(graph, forkJoinGraph);
        }
        return new DrawableConnector(graph, componentToAdd);
    }

}

package com.esb.plugin.designer.editor.designer;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphChangeAware;
import com.esb.plugin.designer.graph.action.AddDrawableToGraph;
import com.esb.plugin.designer.graph.connector.Connector;
import com.esb.plugin.designer.graph.connector.DrawableConnector;
import com.esb.plugin.designer.graph.drawable.Drawable;

import java.awt.*;

abstract class AbstractActionHandler {

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
        /**
         if (componentToAdd instanceof ChoiceDrawable) {
         FlowGraph choiceGraph = new FlowGraphImpl();
         choiceGraph.root(componentToAdd);
         FlowReferenceDrawable placeholderDrawable = new FlowReferenceDrawable(new ComponentDescriptor("Flow ref"));
         choiceGraph.add(componentToAdd, placeholderDrawable);
         ((ChoiceDrawable) componentToAdd).addToScope(placeholderDrawable);
         return new ScopeDrawableConnector(graph, choiceGraph);
         }
         if (componentToAdd instanceof ForkJoinDrawable) {
         FlowGraph choiceGraph = new FlowGraphImpl();
         choiceGraph.root(componentToAdd);
         FlowReferenceDrawable placeholderDrawable = new FlowReferenceDrawable(new ComponentDescriptor("Flow ref"));
         choiceGraph.add(componentToAdd, placeholderDrawable);
         ((ForkJoinDrawable) componentToAdd).addToScope(placeholderDrawable);
         return new ScopeDrawableConnector(graph, choiceGraph);
         }*/
        return new DrawableConnector(graph, componentToAdd);
    }

}

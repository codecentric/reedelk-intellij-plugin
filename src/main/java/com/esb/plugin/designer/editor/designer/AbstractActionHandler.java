package com.esb.plugin.designer.editor.designer;

import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.FlowGraphChangeAware;
import com.esb.plugin.designer.graph.FlowGraphImpl;
import com.esb.plugin.designer.graph.action.AddDrawableToGraph;
import com.esb.plugin.designer.graph.connector.ChoiceConnector;
import com.esb.plugin.designer.graph.connector.Connector;
import com.esb.plugin.designer.graph.connector.DrawableConnector;
import com.esb.plugin.designer.graph.drawable.ChoiceDrawable;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.FlowReferenceDrawable;

import java.awt.*;

abstract class AbstractActionHandler {

    // TODO: Here you should provide a proper constructor for connector.
    protected Connector createComponentConnector(Drawable componentToAdd, FlowGraph graph) {
        if (componentToAdd instanceof ChoiceDrawable) {
            FlowGraph choiceGraph = new FlowGraphImpl();
            choiceGraph.root(componentToAdd);
            FlowReferenceDrawable placeholderDrawable = new FlowReferenceDrawable(new Component("Flow ref"));
            choiceGraph.add(componentToAdd, placeholderDrawable);
            ((ChoiceDrawable) componentToAdd).addToScope(placeholderDrawable);
            return new ChoiceConnector(graph, choiceGraph);
        }
        return new DrawableConnector(graph, componentToAdd);
    }

    protected FlowGraphChangeAware addDrawableToGraph(FlowGraph graph, Drawable dropped, Point dropPoint, Graphics2D graphics) {
        FlowGraphChangeAware modifiableGraph = new FlowGraphChangeAware(graph);
        Connector connector = createComponentConnector(dropped, modifiableGraph);
        AddDrawableToGraph componentAdder = new AddDrawableToGraph(modifiableGraph, dropPoint, connector, graphics);
        componentAdder.add();
        return modifiableGraph;
    }

}

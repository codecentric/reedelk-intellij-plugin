package com.esb.plugin.designer.graph.dragdrop;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.DrawableFactory;

import java.awt.*;

public class NewNodeAdder extends AbstractNodeAdder {

    public NewNodeAdder(FlowGraph graph, Point dropPoint, String componentName) {
        super(graph, dropPoint, DrawableFactory.get(componentName));
    }
}

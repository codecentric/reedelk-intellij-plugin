package com.esb.plugin.designer.graph.dnd;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;

import java.awt.*;

public class ExistingNodeAdder extends AbstractNodeAdder {

    public ExistingNodeAdder(FlowGraph graph, Point dropPoint, Drawable nodeToAdd) {
        super(graph, dropPoint, nodeToAdd);
    }
}

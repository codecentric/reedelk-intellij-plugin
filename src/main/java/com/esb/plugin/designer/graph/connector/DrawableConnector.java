package com.esb.plugin.designer.graph.connector;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

public class DrawableConnector implements Connector {

    private final Drawable drawable;
    private final FlowGraph graph;

    public DrawableConnector(final FlowGraph graph, final Drawable drawable) {
        this.graph = graph;
        this.drawable = drawable;
    }

    @Override
    public void addSuccessor(Drawable successor) {
        graph.add(drawable, successor);
    }

    @Override
    public void addPredecessor(Drawable predecessor) {
        graph.add(predecessor, drawable);
    }

    @Override
    public void add() {
        graph.add(drawable);
    }

    @Override
    public void root() {
        graph.root(drawable);
    }

    @Override
    public void addToScope(ScopedDrawable scope) {
        scope.addToScope(drawable);
    }

    @Override
    public void addPredecessor(ScopedDrawable predecessor, int index) {
        graph.add(predecessor, drawable, index);
    }
}

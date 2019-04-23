package com.esb.plugin.designer.graph.action.strategy;

import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.connector.Connector;
import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopeBoundaries;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;
import com.esb.plugin.designer.graph.scope.FindScopes;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

abstract class AbstractAddStrategy implements AddStrategy {

    protected final FlowGraph graph;
    protected final Point dropPoint;
    protected final Connector connector;
    protected final Graphics2D graphics;

    public AbstractAddStrategy(FlowGraph graph, Point dropPoint, Connector connector, Graphics2D graphics) {
        this.graph = graph;
        this.graphics = graphics;
        this.dropPoint = dropPoint;
        this.connector = connector;
    }

    protected void addToScopeIfNeeded(Drawable closestPrecedingNode) {
        if (closestPrecedingNode instanceof ScopedDrawable) {
            ScopedDrawable scopedDrawable = (ScopedDrawable) closestPrecedingNode;
            connector.addToScope(scopedDrawable);
        } else {
            List<ScopedDrawable> scopedDrawableObjects = FindScopes.of(graph, closestPrecedingNode);
            scopedDrawableObjects.forEach(connector::addToScope);
        }
    }

    static int getScopeMaxXBound(@NotNull FlowGraph graph, @NotNull ScopedDrawable scopedDrawable, @NotNull Graphics2D graphics) {
        ScopeBoundaries scopeBoundaries = scopedDrawable.getScopeBoundaries(graph, graphics);
        return scopeBoundaries.getX() + scopeBoundaries.getWidth();
    }
}

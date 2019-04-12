package com.esb.plugin.designer.graph.drawable;

import com.esb.plugin.designer.graph.FlowGraph;

import java.awt.*;
import java.util.Collection;

public interface ScopedDrawable extends Drawable {

    int VERTICAL_PADDING = 5;
    int HORIZONTAL_PADDING = 5;

    void addToScope(Drawable drawable);

    void removeFromScope(Drawable drawable);

    Collection<Drawable> getScope();

    boolean scopeContains(Drawable drawable);

    ScopeBoundaries getScopeBoundaries(FlowGraph graph, Graphics2D graphics);

}

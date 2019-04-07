package com.esb.plugin.designer.graph.drawable;

import java.util.Collection;

public interface ScopedDrawable extends Drawable {

    int VERTICAL_PADDING = 5;
    int HORIZONTAL_PADDING = 5;

    void addToScope(Drawable drawable);

    void removeFromScope(Drawable drawable);

    Collection<Drawable> getScope();

    boolean scopeContains(Drawable drawable);

}

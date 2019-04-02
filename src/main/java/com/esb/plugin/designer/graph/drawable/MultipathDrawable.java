package com.esb.plugin.designer.graph.drawable;

import java.util.Collection;

public interface MultipathDrawable {

    void addToScope(Drawable drawable);

    void removeFromScope(Drawable drawable);

    Collection<Drawable> getScope();
}

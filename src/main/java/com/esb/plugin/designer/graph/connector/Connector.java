package com.esb.plugin.designer.graph.connector;

import com.esb.plugin.designer.graph.drawable.Drawable;
import com.esb.plugin.designer.graph.drawable.ScopedDrawable;

public interface Connector {

    void addSuccessor(Drawable successor);

    void addPredecessor(Drawable predecessor);

    void add();

    void root();

    void addToScope(ScopedDrawable scope);

    void addPredecessor(ScopedDrawable predecessor, int index);
}

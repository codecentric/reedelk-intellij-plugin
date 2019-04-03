package com.esb.plugin.designer.graph.drawable;

import java.util.Collection;

public interface ScopedDrawable extends Drawable {

    void add(Drawable drawable);

    void remove(Drawable drawable);

    Collection<Drawable> listDrawables();

}

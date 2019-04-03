package com.esb.plugin.designer.graph.drawable;

import com.esb.plugin.designer.editor.component.Component;

import java.util.Collection;

public class ForkJoinDrawable extends AbstractDrawable implements ScopedDrawable {

    public ForkJoinDrawable(Component component) {
        super(component);
    }

    @Override
    public void add(Drawable drawable) {

    }

    @Override
    public void remove(Drawable drawable) {

    }

    @Override
    public Collection<Drawable> listDrawables() {
        return null;
    }
}

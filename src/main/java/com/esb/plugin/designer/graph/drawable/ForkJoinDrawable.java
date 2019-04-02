package com.esb.plugin.designer.graph.drawable;

import com.esb.plugin.designer.editor.component.Component;

import java.util.Collection;

public class ForkJoinDrawable extends AbstractDrawable implements MultipathDrawable {

    public ForkJoinDrawable(Component component) {
        super(component);
    }

    @Override
    public void addToScope(Drawable drawable) {

    }

    @Override
    public void removeFromScope(Drawable drawable) {

    }

    @Override
    public Collection<Drawable> getScope() {
        return null;
    }
}

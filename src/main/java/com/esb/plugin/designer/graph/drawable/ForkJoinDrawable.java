package com.esb.plugin.designer.graph.drawable;

import com.esb.plugin.designer.editor.component.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ForkJoinDrawable extends AbstractDrawable implements ScopedDrawable {

    private Set<Drawable> scope = new HashSet<>();

    public ForkJoinDrawable(Component component) {
        super(component);
    }

    public void add(Drawable drawable) {
        this.scope.add(drawable);
    }

    public void remove(Drawable drawable) {
        this.scope.remove(drawable);
    }

    public Collection<Drawable> getDrawablesInScope() {
        return Collections.unmodifiableSet(scope);
    }

    @Override
    public boolean contains(Drawable drawable) {
        return scope.contains(drawable);
    }

}

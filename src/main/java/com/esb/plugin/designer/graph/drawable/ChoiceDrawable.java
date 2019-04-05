package com.esb.plugin.designer.graph.drawable;

import com.esb.plugin.designer.editor.component.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ChoiceDrawable extends AbstractDrawable implements ScopedDrawable {

    private Set<Drawable> scope = new HashSet<>();

    public ChoiceDrawable(Component component) {
        super(component);
    }

    public void addToScope(Drawable drawable) {
        this.scope.add(drawable);
    }

    public void removeFromScope(Drawable drawable) {
        this.scope.remove(drawable);
    }

    public Collection<Drawable> getScope() {
        return Collections.unmodifiableSet(scope);
    }

    @Override
    public boolean scopeContains(Drawable drawable) {
        return scope.contains(drawable);
    }

}

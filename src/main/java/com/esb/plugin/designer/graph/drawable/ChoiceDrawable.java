package com.esb.plugin.designer.graph.drawable;

import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.decorators.ScopeBoundariesDrawable;
import com.esb.plugin.designer.graph.drawable.decorators.VerticalDivider;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ChoiceDrawable extends AbstractDrawable implements ScopedDrawable {

    private final Drawable verticalDivider;
    private final Drawable scopeBoundariesDrawable;

    private Set<Drawable> scope = new HashSet<>();

    public ChoiceDrawable(Component component) {
        super(component);
        this.verticalDivider = new VerticalDivider(this);
        this.scopeBoundariesDrawable = new ScopeBoundariesDrawable(this);
    }

    @Override
    public boolean scopeContains(Drawable drawable) {
        return scope.contains(drawable);
    }

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        super.draw(graph, graphics, observer);
        verticalDivider.draw(graph, graphics, observer);
        scopeBoundariesDrawable.draw(graph, graphics, observer);
    }

    @Override
    public void addToScope(Drawable drawable) {
        this.scope.add(drawable);
    }

    @Override
    public void removeFromScope(Drawable drawable) {
        this.scope.remove(drawable);
    }

    @Override
    public Collection<Drawable> getScope() {
        return Collections.unmodifiableSet(scope);
    }

}

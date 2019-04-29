package com.esb.plugin.designer.graph.drawable.decorators;

import com.esb.plugin.designer.editor.component.Component;
import com.esb.plugin.designer.graph.FlowGraph;
import com.esb.plugin.designer.graph.drawable.ComponentAware;

import java.awt.*;
import java.awt.image.ImageObserver;

public class NothingSelectedDrawable extends AbstractDrawable implements ComponentAware {

    @Override
    public void draw(FlowGraph graph, Graphics2D graphics, ImageObserver observer) {
        // nothing to draw
    }

    @Override
    public boolean isSelected() {
        return false;
    }

    @Override
    public Component component() {
        return null;
    }
}

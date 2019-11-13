package com.reedelk.plugin.editor.designer.widget;

import com.reedelk.plugin.component.domain.ComponentData;

import java.awt.*;
import java.awt.image.ImageObserver;

public class IconDragging extends Icon {

    private static final float alpha = 0.8f; //draw half transparent
    private static final AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);

    public IconDragging(ComponentData componentData) {
        super(componentData);
    }

    @Override
    public void draw(Graphics2D graphics, ImageObserver observer) {
        graphics.setComposite(ac);
        super.draw(graphics, observer);
    }
}

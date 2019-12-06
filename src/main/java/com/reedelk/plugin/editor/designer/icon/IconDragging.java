package com.reedelk.plugin.editor.designer.icon;

import com.reedelk.plugin.component.domain.ComponentData;

import java.awt.*;
import java.awt.image.ImageObserver;

public class IconDragging extends Icon {

    private static final float ALPHA = 0.8f; //draw half transparent
    private static final AlphaComposite ALPHA_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ALPHA);

    public IconDragging(ComponentData componentData) {
        super(componentData);
    }

    @Override
    public void draw(Graphics2D graphics, ImageObserver observer) {
        graphics.setComposite(ALPHA_COMPOSITE);
        super.draw(graphics, observer);
    }
}

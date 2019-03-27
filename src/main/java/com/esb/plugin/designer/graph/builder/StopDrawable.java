package com.esb.plugin.designer.graph.builder;

import com.esb.plugin.designer.editor.component.Component;

import java.awt.*;
import java.awt.image.ImageObserver;

public class StopDrawable extends AbstractDrawable {

    public StopDrawable() {
        super(new Component("Stop"));
    }


    @Override
    public void draw(Graphics graphics, ImageObserver observer) {

    }

    @Override
    public int width(ImageObserver imageObserver) {
        return 0;
    }

    @Override
    public int height(ImageObserver imageObserver) {
        return 0;
    }
}

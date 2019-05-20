package com.esb.plugin.designer.canvas.drawables;

import com.esb.plugin.component.ComponentData;
import com.intellij.ui.JBColor;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;

import static com.esb.plugin.component.ComponentDescriptionDecorator.DESCRIPTION_PROPERTY_NAME;


public class ComponentDescription extends AbstractText {

    private ComponentData componentData;

    public ComponentDescription(ComponentData componentData) {
        this.componentData = componentData;
    }

    @Override
    protected String getText() {
        return (String) componentData.getOrDefault(DESCRIPTION_PROPERTY_NAME, StringUtils.EMPTY);
    }

    @Override
    protected Color getColor() {
        return JBColor.LIGHT_GRAY;
    }
}

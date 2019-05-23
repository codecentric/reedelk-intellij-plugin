package com.esb.plugin.editor.designer.drawables;

import com.esb.plugin.component.domain.ComponentData;
import com.intellij.ui.JBColor;

import java.awt.*;

import static com.esb.plugin.component.domain.ComponentDescriptionDecorator.DESCRIPTION_PROPERTY_NAME;


public class ComponentDescription extends AbstractText {

    private ComponentData componentData;

    public ComponentDescription(ComponentData componentData) {
        this.componentData = componentData;
    }

    @Override
    protected String getText() {
        return (String) componentData.get(DESCRIPTION_PROPERTY_NAME);
    }

    @Override
    protected Color getColor() {
        return JBColor.LIGHT_GRAY;
    }
}

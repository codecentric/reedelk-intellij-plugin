package com.esb.plugin.editor.designer.widget;

import com.esb.internal.commons.JsonParser;
import com.esb.plugin.commons.Fonts;
import com.esb.plugin.component.domain.ComponentData;
import com.intellij.ui.JBColor;

import java.awt.*;

public class TextComponentDescription extends AbstractText {

    private ComponentData componentData;

    public TextComponentDescription(ComponentData componentData) {
        super(Fonts.COMPONENT_DESCRIPTION);
        this.componentData = componentData;
    }

    @Override
    protected String getText() {
        return (String) componentData.get(JsonParser.Implementor.description());
    }

    @Override
    protected Color getColor() {
        return JBColor.LIGHT_GRAY;
    }

    @Override
    protected Color getSelectedColor() {
        return JBColor.GRAY;
    }
}

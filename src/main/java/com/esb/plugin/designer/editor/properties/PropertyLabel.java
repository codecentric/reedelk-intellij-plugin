package com.esb.plugin.designer.editor.properties;

import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;

import java.awt.*;

public class PropertyLabel extends JBLabel {

    PropertyLabel(String text) {
        super(text);
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setBorder(JBUI.Borders.emptyRight(5));
    }
}
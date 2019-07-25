package com.esb.plugin.editor.properties;

import com.esb.plugin.commons.Colors;
import com.intellij.ui.components.JBPanel;

import java.awt.*;

public class PropertiesBasePanel extends JBPanel {

    public PropertiesBasePanel(LayoutManager layout, boolean isDoubleBuffered) {
        super(layout, isDoubleBuffered);
        setBackground(Colors.PROPERTIES_BACKGROUND);
    }

    public PropertiesBasePanel(LayoutManager layout) {
        super(layout);
        setBackground(Colors.PROPERTIES_BACKGROUND);
    }

    public PropertiesBasePanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
        setBackground(Colors.PROPERTIES_BACKGROUND);
    }

    public PropertiesBasePanel() {
        super();
        setBackground(Colors.PROPERTIES_BACKGROUND);
    }
}

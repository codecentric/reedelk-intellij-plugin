package com.esb.plugin.editor.properties.widget;

import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;

public class DefaultPropertiesPanel extends JBPanel {

    public DefaultPropertiesPanel() {
        super(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
    }
}

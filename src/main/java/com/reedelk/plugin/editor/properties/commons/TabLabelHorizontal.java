package com.reedelk.plugin.editor.properties.commons;

import com.reedelk.plugin.commons.Colors;

import javax.swing.*;

public class TabLabelHorizontal extends JLabel {

    public TabLabelHorizontal(String name) {
        super(name, SwingConstants.CENTER);
        setForeground(Colors.PROPERTIES_TABS_TITLE);
    }
}

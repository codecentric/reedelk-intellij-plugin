package com.reedelk.plugin.editor.properties.commons;

import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.Sizes;

import javax.swing.*;

public class TabLabel extends JLabel {

    public TabLabel(String name) {
        super(name, SwingConstants.RIGHT);
        setPreferredSize(Sizes.TabbedPane.TAB_LABEL);
        setForeground(Colors.PROPERTIES_TABS_TITLE);
    }
}

package com.reedelk.plugin.editor.properties.commons;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBLabel;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.Sizes;

import javax.swing.*;
import java.awt.*;

public class TabLabelVertical extends JPanel {

    public TabLabelVertical(String name) {
        super(new BorderLayout());
        setOpaque(false);

        JBLabel tabTitle = new JBLabel(name, JLabel.RIGHT);
        tabTitle.setForeground(Colors.PROPERTIES_TABS_TITLE);
        add(tabTitle, BorderLayout.WEST);

        Icon icon = AllIcons.General.ArrowRight;
        add(new JBLabel(icon, JLabel.RIGHT), BorderLayout.EAST);
        
        setPreferredSize(Sizes.TabbedPane.TAB_LABEL);
    }
}

package de.codecentric.reedelk.plugin.editor.properties.commons;

import com.intellij.icons.AllIcons;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import de.codecentric.reedelk.plugin.commons.Colors;
import de.codecentric.reedelk.plugin.commons.Sizes;

import javax.swing.*;
import java.awt.*;

public class TabLabelVertical extends JPanel {

    public TabLabelVertical(String name) {
        super(new BorderLayout());
        setOpaque(false);

        JBLabel tabTitle = new JBLabel(name, JLabel.LEFT);
        tabTitle.setBorder(JBUI.Borders.emptyLeft(5));
        tabTitle.setForeground(Colors.PROPERTIES_TABS_TITLE);

        add(tabTitle, BorderLayout.CENTER);

        Icon icon = AllIcons.General.ArrowRight;
        add(new JBLabel(icon, JLabel.LEFT), BorderLayout.WEST);
        
        setPreferredSize(Sizes.TabbedPane.TAB_LABEL);
    }
}

package com.reedelk.plugin.editor.properties.commons;

import com.intellij.ui.components.JBLabel;
import com.reedelk.plugin.commons.Colors;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders.empty;
import static com.reedelk.plugin.editor.properties.commons.ClickableLabel.IconAlignment.RIGHT;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;

public class TypeObjectContainerHeader extends DisposablePanel {

    public TypeObjectContainerHeader(String displayName, Icon icon, ClickableLabel.OnClickAction labelClickListener) {
        this(new ClickableLabel(displayName, icon, RIGHT,labelClickListener));
    }

    public TypeObjectContainerHeader(String displayName) {
        this(new JBLabel(displayName));
    }

    private TypeObjectContainerHeader(JLabel label) {
        label.setForeground(Colors.TOOL_WINDOW_PROPERTIES_TEXT);
        HorizontalSeparator separator = new HorizontalSeparator();
        setLayout(new BorderLayout());
        add(label, WEST);
        add(separator, CENTER);
    }

    private static class HorizontalSeparator extends DisposablePanel {
        public HorizontalSeparator() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridwidth = GridBagConstraints.REMAINDER;
            gbc.weightx = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JSeparator jSeparator = new JSeparator();
            jSeparator.setForeground(Colors.TYPE_OBJECT_HORIZONTAL_SEPARATOR);
            add(jSeparator, gbc);

            setBorder(BORDER_HORIZONTAL_SEPARATOR_CONTAINER);
        }
    }

    static final Border BORDER_HORIZONTAL_SEPARATOR_CONTAINER = empty(2, 5, 0, 0);
}
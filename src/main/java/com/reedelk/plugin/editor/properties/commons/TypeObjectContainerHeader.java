package com.reedelk.plugin.editor.properties.commons;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
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
        JLabel switchLabel = new ClickableLabel(displayName, icon, RIGHT,labelClickListener);
        HorizontalSeparator separator = new HorizontalSeparator();
        setLayout(new BorderLayout());
        add(switchLabel, WEST);
        add(separator, CENTER);
    }

    public TypeObjectContainerHeader(String displayName) {
        JLabel titleLabel = new JBLabel(displayName);
        HorizontalSeparator separator = new HorizontalSeparator();
        setLayout(new BorderLayout());
        add(titleLabel, WEST);
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
            jSeparator.setBorder(JBUI.Borders.customLine(JBColor.LIGHT_GRAY, 2));
            add(jSeparator, gbc);

            setBorder(BORDER_HORIZONTAL_SEPARATOR);
        }
    }

    static final Border BORDER_HORIZONTAL_SEPARATOR = empty(2, 5, 0, 0);
}

package com.reedelk.plugin.editor.properties.commons;

import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.ClickableLabelWithTooltip;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Optional;

import static com.reedelk.plugin.commons.Colors.TOOL_WINDOW_PROPERTIES_TEXT;
import static com.reedelk.plugin.commons.Icons.Misc.Info;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;

public class PropertyTitleLabel extends JPanel {

    private static final Border BORDER_PROPERTY_TITLE = JBUI.Borders.emptyRight(2);

    public PropertyTitleLabel(String propertyDisplayName) {
        JBLabel textLabel = new JBLabel(propertyDisplayName + ":");
        textLabel.setBorder(BORDER_PROPERTY_TITLE);
        textLabel.setForeground(TOOL_WINDOW_PROPERTIES_TEXT);

        setLayout(new BorderLayout());
        add(textLabel, WEST);
    }

    public PropertyTitleLabel(ComponentPropertyDescriptor propertyDescriptor) {
        this(propertyDescriptor.getDisplayName());
        Optional.ofNullable(propertyDescriptor.getPropertyInfo()).ifPresent(propertyInfoText -> {
            JLabel infoTooltipIcon = new ClickableLabelWithTooltip(propertyInfoText, Info);
            add(ContainerFactory.pushLeft(infoTooltipIcon), CENTER);
        });
    }
}
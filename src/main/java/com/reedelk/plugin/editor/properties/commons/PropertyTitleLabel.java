package com.reedelk.plugin.editor.properties.commons;

import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.plugin.commons.TooltipContent;
import com.reedelk.plugin.editor.properties.ClickableTooltip;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import static com.reedelk.plugin.commons.Colors.TOOL_WINDOW_PROPERTIES_TEXT;
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

    public PropertyTitleLabel(PropertyDescriptor propertyDescriptor) {
        this(propertyDescriptor.getDisplayName());
        TooltipContent.from(propertyDescriptor).build().ifPresent(toolTipText -> {
            ClickableTooltip tooltip = new ClickableTooltip(toolTipText);
            add(ContainerFactory.pushLeft(tooltip), CENTER);
        });
    }
}

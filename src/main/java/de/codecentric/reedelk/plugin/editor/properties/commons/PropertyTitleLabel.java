package de.codecentric.reedelk.plugin.editor.properties.commons;

import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import de.codecentric.reedelk.plugin.commons.TooltipContent;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

import static de.codecentric.reedelk.plugin.commons.Colors.PROPERTIES_PROPERTY_TITLE;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;

public class PropertyTitleLabel extends JPanel {

    private static final Border BORDER_PROPERTY_TITLE = JBUI.Borders.emptyRight(2);

    public PropertyTitleLabel(String propertyDisplayName) {
        JBLabel textLabel = new JBLabel(createLabelText(propertyDisplayName));
        textLabel.setBorder(BORDER_PROPERTY_TITLE);
        textLabel.setForeground(PROPERTIES_PROPERTY_TITLE);

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

    protected String createLabelText(String propertyDisplayName) {
        return propertyDisplayName + ":";
    }
}

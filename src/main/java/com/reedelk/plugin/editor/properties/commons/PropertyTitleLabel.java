package com.reedelk.plugin.editor.properties.commons;

import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.ClickableLabelWithToolTip1;

import javax.swing.*;
import java.awt.*;

import static com.reedelk.plugin.commons.Icons.Misc.Info;

public class PropertyTitleLabel extends JPanel {

    public PropertyTitleLabel(String propertyDisplayName) {
        JLabel textLabel = new JBLabel(propertyDisplayName + ":");
        textLabel.setBorder(JBUI.Borders.emptyRight(2));

        setLayout(new BorderLayout());
        add(textLabel, BorderLayout.WEST);
    }

    public PropertyTitleLabel(ComponentPropertyDescriptor propertyDescriptor) {
        this(propertyDescriptor.getDisplayName());
        propertyDescriptor.getPropertyInfo().ifPresent(propertyInfoText -> {
            JLabel infoTooltipIcon = new ClickableLabelWithToolTip1(propertyInfoText, Info);
            add(ContainerFactory.pushLeft(infoTooltipIcon), BorderLayout.CENTER);
        });
    }
}

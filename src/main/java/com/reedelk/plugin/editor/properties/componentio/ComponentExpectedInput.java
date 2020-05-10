package com.reedelk.plugin.editor.properties.componentio;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.editor.properties.commons.*;
import com.reedelk.plugin.service.module.impl.component.componentio.IOComponent;
import com.reedelk.plugin.service.module.impl.component.componentio.OnComponentIO;
import com.reedelk.runtime.api.commons.StringUtils;

import javax.swing.*;
import java.awt.*;

public class ComponentExpectedInput extends DisposableScrollPane implements OnComponentIO {

    private static final int LEFT_OFFSET = 24;

    public ComponentExpectedInput() {
        setBorder(JBUI.Borders.empty(0, 1, 0, 8));
        setBackground(JBColor.WHITE);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

    @Override
    public void onComponentIO(String inputFQCN, String outputFQCN, IOComponent IOComponent) {
        DisposablePanel theContent = new DisposablePanel(new GridBagLayout());
        theContent.setBackground(JBColor.WHITE);
        DisposablePanel panel = ContainerFactory.pushTop(theContent);
        panel.setBackground(JBColor.WHITE);
        panel.setBorder(JBUI.Borders.empty(5, 2));
        setViewportView(panel);

        int topOffset = 0;
        if (StringUtils.isNotBlank(IOComponent.getPayloadDescription())) {
            DisposableCollapsiblePane description = new DisposableCollapsiblePane("Description", () -> {
                JBLabel label = new JBLabel("Test my label");
                label.setBorder(JBUI.Borders.emptyLeft(LEFT_OFFSET));
                return label;
            });
            topOffset = 4;
            description.setBorder(JBUI.Borders.empty());
            FormBuilder.get().addFullWidthAndHeight(description, theContent);
        }
    }
}

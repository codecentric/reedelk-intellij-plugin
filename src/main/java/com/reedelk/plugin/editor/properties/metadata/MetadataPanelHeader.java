package com.reedelk.plugin.editor.properties.metadata;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders.customLine;
import static com.reedelk.plugin.commons.Colors.SCRIPT_EDITOR_CONTEXT_PANEL_TITLE_BG;

public class MetadataPanelHeader extends DisposablePanel {

    public MetadataPanelHeader(String header1Text, ClickableLabel.OnClickAction header1Action,
                               String header2Text, ClickableLabel.OnClickAction header2Action) {
        super(new BorderLayout());

        ClickableLabel header1Label = new ClickableLabel(header1Text);
        ClickableLabel header2Label = new ClickableLabel(header2Text);

        header1Label.setAction(() -> {
            setActive(header1Label);
            setNotActive(header2Label, true);
            header1Action.onClick();
        });

        header2Label.setAction(() -> {
            setActive(header2Label);
            setNotActive(header1Label, false);
            header2Action.onClick();
        });

        setActive(header1Label);
        setNotActive(header2Label, true);

        DisposablePanel panel = ContainerFactory.pushLeft(header2Label);
        Box horizontalBox = Box.createHorizontalBox();
        Border bottomLine = customLine(JBColor.LIGHT_GRAY, 0, 0, 1, 0);
        horizontalBox.setBorder(bottomLine);
        panel.add(horizontalBox, BorderLayout.CENTER);

        add(header1Label, BorderLayout.WEST);
        add(panel, BorderLayout.CENTER);

        setPreferredSize(new Dimension(200, 27));
        setBackground(SCRIPT_EDITOR_CONTEXT_PANEL_TITLE_BG);
    }

    private void setActive(JComponent component) {
        component.setForeground(JBColor.DARK_GRAY);
        // Add right border
        Border line = customLine(JBColor.LIGHT_GRAY, 0, 0, 0, 1);
        Border bottomLine = customLine(new Color(59, 121, 197, 200), 0,0, 3, 0);
        Border padding = JBUI.Borders.empty(0, 10);
        component.setBorder(new CompoundBorder(new CompoundBorder(bottomLine, line), padding));
    }

    private void setNotActive(JComponent component, boolean isLast) {
        component.setForeground(JBColor.DARK_GRAY);
        // Add right border
        Border line = customLine(JBColor.LIGHT_GRAY, 0, 0, 0, 1);
        Border padding = JBUI.Borders.empty(0, 10, 2, 10);
        Border bottomLine = customLine(JBColor.LIGHT_GRAY, 0, 0, 1, 0);
        component.setBorder(new CompoundBorder(new CompoundBorder(bottomLine, line), padding));
    }
}

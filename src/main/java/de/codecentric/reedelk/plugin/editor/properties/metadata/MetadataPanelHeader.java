package de.codecentric.reedelk.plugin.editor.properties.metadata;

import de.codecentric.reedelk.plugin.editor.properties.commons.ClickableLabel;
import de.codecentric.reedelk.plugin.editor.properties.commons.ContainerFactory;
import de.codecentric.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.intellij.ui.JBColor;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders.customLine;
import static de.codecentric.reedelk.plugin.commons.Colors.INPUT_OUTPUT_PANEL_BTN_BOTTOM_BORDER;
import static de.codecentric.reedelk.plugin.commons.Colors.INPUT_OUTPUT_PANEL_BTN_ON_HOVER_BG;

public class MetadataPanelHeader extends DisposablePanel {

    private JComponent active;

    public MetadataPanelHeader(String header1Text, ClickableLabel.OnClickAction header1Action,
                               String header2Text, ClickableLabel.OnClickAction header2Action) {
        super(new BorderLayout());

        ClickableLabel header1Label = new ClickableLabel(header1Text);
        ClickableLabel header2Label = new ClickableLabel(header2Text);

        header1Label.setAction(() -> {
            setActive(header1Label);
            setNotActive(header2Label);
            header1Action.onClick();
        });

        header2Label.setAction(() -> {
            setActive(header2Label);
            setNotActive(header1Label);
            header2Action.onClick();
        });

        header1Label.setBackground(INPUT_OUTPUT_PANEL_BTN_ON_HOVER_BG);
        header2Label.setBackground(INPUT_OUTPUT_PANEL_BTN_ON_HOVER_BG);
        setOnHoverAndExit(header1Label);
        setOnHoverAndExit(header2Label);
        setActive(header1Label);
        setNotActive(header2Label);

        DisposablePanel panel = ContainerFactory.pushLeft(header2Label);
        Box horizontalBox = Box.createHorizontalBox();
        Border bottomLine = customLine(JBColor.LIGHT_GRAY, 0, 0, 1, 0);
        horizontalBox.setBorder(bottomLine);
        panel.add(horizontalBox, BorderLayout.CENTER);

        add(header1Label, BorderLayout.WEST);
        add(panel, BorderLayout.CENTER);

        setPreferredSize(new Dimension(200, 27));
        setOpaque(false);
    }

    private void setActive(JComponent component) {
        active = component;
        // Add Bottom and right border
        Border line = customLine(JBColor.LIGHT_GRAY, 0, 0, 0, 1);
        Border bottomLine = customLine(INPUT_OUTPUT_PANEL_BTN_BOTTOM_BORDER, 0,0, 3, 0);
        Border padding = JBUI.Borders.empty(0, 10);
        component.setBorder(new CompoundBorder(new CompoundBorder(bottomLine, line), padding));
        component.setForeground(JBColor.DARK_GRAY);
        component.setOpaque(false);
    }

    private void setNotActive(JComponent component) {
        component.setForeground(JBColor.DARK_GRAY);
        // Add right border
        Border line = customLine(JBColor.LIGHT_GRAY, 0, 0, 0, 1);
        Border padding = JBUI.Borders.empty(0, 10, 2, 10);
        Border bottomLine = customLine(JBColor.LIGHT_GRAY, 0, 0, 1, 0);
        component.setBorder(new CompoundBorder(new CompoundBorder(bottomLine, line), padding));
    }

    private void setOnHoverAndExit(ClickableLabel label) {
        label.setOnHover(() -> {
            if (active != label) {
                label.setOpaque(true);
                label.revalidate();
                label.repaint();
            }
        });

        label.setOnExit(() -> {
            label.setOpaque(false);
            label.revalidate();
            label.repaint();
        });
    }
}

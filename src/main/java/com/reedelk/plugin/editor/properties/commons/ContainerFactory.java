package com.reedelk.plugin.editor.properties.commons;

import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.commons.Colors;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

import static java.awt.BorderLayout.*;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

public class ContainerFactory {

    private ContainerFactory() {
    }

    public static DisposablePanel createLabelNextToComponent(JLabel label, JComponent body) {
        return createLabelNextToComponent(label, body, true);
    }

    static DisposablePanel createLabelNextToComponent(JLabel label, JComponent body, boolean outerBorder) {
        Border iconBorder;
        if (outerBorder) {
            Border iconOutside = JBUI.Borders.customLine(Colors.SCRIPT_EDITOR_INLINE_ICON_BORDER, 1, 1, 1, 0);
            Border iconInside = JBUI.Borders.empty(0, 4);
            iconBorder = new CompoundBorder(iconOutside, iconInside);
        } else {
            Border iconOutside = JBUI.Borders.customLine(Colors.SCRIPT_EDITOR_INLINE_ICON_BORDER, 0, 0, 0, 1);
            Border iconInside = JBUI.Borders.empty(0, 4);
            iconBorder = new CompoundBorder(iconOutside, iconInside);
        }

        label.setBorder(iconBorder);

        Border bodyBorder;
        if (outerBorder) {
            Border bodyOutside = JBUI.Borders.customLine(Colors.SCRIPT_EDITOR_INLINE_ICON_BORDER, 1, 1, 1, 1);
            Border bodyInside = JBUI.Borders.empty(0, 2);
            bodyBorder = new CompoundBorder(bodyOutside, bodyInside);
        } else {
            bodyBorder = JBUI.Borders.empty(0, 2);
        }
        body.setBorder(bodyBorder);


        DisposablePanel wrapper = new DisposablePanel(new BorderLayout());
        wrapper.add(label, WEST);
        wrapper.add(body, CENTER);
        return wrapper;
    }

    public static DisposablePanel createObjectTypeContainer(@NotNull JComponent renderedComponent, @NotNull String title) {
        return new DefaultObjectTypeContainer(renderedComponent, title);
    }

    public static DisposablePanel pushTop(JComponent component) {
        DisposablePanel panel = new DisposablePanel();
        panel.setLayout(new BorderLayout());
        panel.add(component, NORTH);
        panel.add(Box.createGlue(), CENTER);
        return panel;
    }

    public static DisposablePanel pushLeft(JComponent component) {
        DisposablePanel panel = new DisposablePanel();
        panel.setLayout(new BorderLayout());
        panel.add(component, WEST);
        panel.add(Box.createGlue(), CENTER);
        return panel;
    }

    public static DisposablePanel pushCenter(JComponent component, Border border) {
        DisposablePanel panel = new DisposablePanel();
        panel.setLayout(new BorderLayout());
        panel.add(component, CENTER);
        panel.setBorder(border);
        return panel;
    }

    public static DisposableScrollPane makeItScrollable(DisposablePanel panel) {
        DisposableScrollPane scrollPane = new DisposableScrollPane();
        scrollPane.setBorder(JBUI.Borders.empty());
        scrollPane.setViewportView(panel);
        scrollPane.createVerticalScrollBar();
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        return scrollPane;
    }
}

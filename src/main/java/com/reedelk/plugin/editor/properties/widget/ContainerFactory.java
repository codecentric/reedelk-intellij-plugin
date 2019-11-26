package com.reedelk.plugin.editor.properties.widget;

import com.intellij.openapi.module.Module;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.component.domain.Collapsible;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.editor.properties.renderer.PropertiesRendererFactory;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

import static java.awt.BorderLayout.*;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class ContainerFactory {

    public static DisposablePanel createLabelNextToComponent(JLabel label, JComponent body) {
        return createLabelNextToComponent(label, body, true);
    }

    public static DisposablePanel createLabelNextToComponent(JLabel label, JComponent body, boolean outerBorder) {
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

    private static class Focusable extends DisposablePanel {

        private final JComponent body;

        Focusable(JLabel icon, JComponent body) {
            super(new BorderLayout());
            add(icon, WEST);
            add(body, CENTER);
            this.body = body;
        }

        @Override
        public void requestFocus() {
            body.requestFocus();
        }
    }

    public static DisposablePanel createObjectTypeContainer(
            @NotNull JComponent renderedComponent,
            @NotNull TypeObjectDescriptor descriptor,
            @NotNull String title) {
        return Collapsible.YES.equals(descriptor.getCollapsible()) ?
                new CollapsibleObjectTypeContainer(renderedComponent, title) :
                createObjectTypeContainer(renderedComponent, title);
    }

    public static DisposablePanel createObjectTypeContainer(
            @NotNull JComponent renderedComponent,
            @NotNull String title) {
        return new DefaultObjectTypeContainer(renderedComponent, title);
    }

    public static DisposableScrollPane createPropertiesPanel(Module module, ComponentData componentData, FlowSnapshot snapshot, GraphNode node) {
        DisposablePanel propertiesPanel = PropertiesRendererFactory.get()
                .component(componentData)
                .snapshot(snapshot)
                .module(module)
                .build()
                .render(node);
        DisposablePanel propertiesBoxContainer = pushTop(propertiesPanel);
        propertiesBoxContainer.setBorder(JBUI.Borders.empty(10));
        return makeItScrollable(propertiesBoxContainer);
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

    public static DisposableScrollPane makeItScrollable(DisposablePanel panel) {
        DisposableScrollPane scrollPane = new DisposableScrollPane();
        scrollPane.setBorder(JBUI.Borders.empty());
        scrollPane.setViewportView(panel);
        scrollPane.createVerticalScrollBar();
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        return scrollPane;
    }
}

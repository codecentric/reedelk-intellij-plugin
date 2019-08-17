package com.reedelk.plugin.editor.properties.widget;

import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.editor.properties.renderer.NodePropertiesRendererFactory;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

import static java.awt.BorderLayout.*;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class ContainerFactory {

    public static DisposablePanel createObjectTypeContainer(String title, JComponent renderedComponent) {
        DisposablePanel container = new DisposablePanel(new BorderLayout());
        Border outsideMargin = JBUI.Borders.emptyTop(3);
        Border border = BorderFactory.createTitledBorder(title);
        Border outside = new CompoundBorder(outsideMargin, border);
        Border margin = JBUI.Borders.empty(3);
        container.setBorder(new CompoundBorder(outside, margin));
        container.add(renderedComponent, CENTER);
        container.add(Box.createHorizontalGlue(), EAST);
        return container;
    }

    public static DisposableScrollPane createPropertiesPanel(Module module, ComponentData componentData, FlowSnapshot snapshot, GraphNode node) {
        DisposablePanel propertiesPanel = NodePropertiesRendererFactory.get()
                .component(componentData)
                .snapshot(snapshot)
                .module(module)
                .build()
                .render(node);
        DisposablePanel propertiesBoxContainer = pushPanelToTop(propertiesPanel);
        propertiesBoxContainer.setBorder(JBUI.Borders.empty(10));
        return wrapInsideScrollPane(propertiesBoxContainer);
    }

    public static DisposablePanel pushPanelToTop(DisposablePanel propertiesPanel) {
        DisposablePanel propertiesBoxContainer = new DisposablePanel();
        propertiesBoxContainer.setLayout(new BorderLayout());
        propertiesBoxContainer.add(propertiesPanel, NORTH);
        propertiesBoxContainer.add(Box.createGlue(), CENTER);
        return propertiesBoxContainer;
    }

    public static JComponent makeItScrollable(DisposablePanel panel, Dimension preferredPanelSize) {
        JBScrollPane scrollPane = new JBScrollPane(panel);
        scrollPane.setBorder(JBUI.Borders.empty());
        scrollPane.setPreferredSize(preferredPanelSize);
        return scrollPane;
    }

    private static DisposableScrollPane wrapInsideScrollPane(DisposablePanel propertiesPanel) {
        DisposableScrollPane scrollPane = new DisposableScrollPane();
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setViewportView(propertiesPanel);
        scrollPane.createVerticalScrollBar();
        scrollPane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
        return scrollPane;
    }
}

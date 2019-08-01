package com.esb.plugin.editor.properties.widget;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.properties.renderer.NodePropertiesRendererFactory;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.intellij.openapi.module.Module;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

import static java.awt.BorderLayout.*;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class ContainerFactory {

    public static DisposablePanel createObjectTypeContainer(String title, JComponent renderedComponent) {
        DisposablePanel container = new DisposablePanel(new BorderLayout());
        Border outsideMargin = JBUI.Borders.emptyTop(15);
        Border border = BorderFactory.createTitledBorder(title);
        Border outside = new CompoundBorder(outsideMargin, border);
        Border margin = JBUI.Borders.empty(5);
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

    private static DisposablePanel pushPanelToTop(DisposablePanel propertiesPanel) {
        DisposablePanel propertiesBoxContainer = new DisposablePanel();
        propertiesBoxContainer.setLayout(new BorderLayout());
        propertiesBoxContainer.add(propertiesPanel, NORTH);
        propertiesBoxContainer.add(Box.createGlue(), CENTER);
        return propertiesBoxContainer;
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

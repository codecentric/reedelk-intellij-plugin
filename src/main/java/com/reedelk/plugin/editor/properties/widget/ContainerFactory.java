package com.reedelk.plugin.editor.properties.widget;

import com.intellij.openapi.module.Module;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.component.domain.Collapsible;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.editor.properties.renderer.PropertiesRendererFactory;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;

public class ContainerFactory {

    public static DisposablePanel createObjectTypeContainer(
            @NotNull String displayName,
            @NotNull TypeObjectDescriptor descriptor,
            @NotNull JComponent renderedComponent) {

        return Collapsible.YES.equals(descriptor.getCollapsible()) ?
                new CollapsibleObjectTypeContainer(renderedComponent, displayName) :
                new DefaultObjectTypeContainer(renderedComponent, displayName);
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
        DisposablePanel propertiesBoxContainer = new DisposablePanel();
        propertiesBoxContainer.setLayout(new BorderLayout());
        propertiesBoxContainer.add(component, NORTH);
        propertiesBoxContainer.add(Box.createGlue(), CENTER);
        return propertiesBoxContainer;
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

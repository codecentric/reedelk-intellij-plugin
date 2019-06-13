package com.esb.plugin.editor.properties;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.designer.SelectListener;
import com.esb.plugin.editor.properties.renderer.node.NodePropertiesRendererFactory;
import com.esb.plugin.editor.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.editor.properties.widget.FormBuilder;
import com.esb.plugin.editor.properties.widget.input.InputField;
import com.esb.plugin.editor.properties.widget.input.StringInputField;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.NothingSelectedNode;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

public class PropertiesPanel extends JBPanel implements SelectListener {

    private final MatteBorder border = BorderFactory.createMatteBorder(0, 10, 0, 0, getBackground());

    private final Module module;
    private final FlowSnapshot snapshot;

    public PropertiesPanel(Module module, FlowSnapshot snapshot) {
        this.module = module;
        this.snapshot = snapshot;
        setBorder(border);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }

    @Override
    public void onSelect(FlowSnapshot snapshot, GraphNode node) {
        if (node instanceof NothingSelectedNode) return;

        ComponentData componentData = node.componentData();
        removeAll();

        JBPanel propertiesPanel = createPropertiesPanel(componentData, snapshot, node);

        Icon icon = componentData.getComponentIcon();

        JBTabbedPane tabbedPane = new JBTabbedPane();
        tabbedPane.addTab(componentData.getDisplayName(), icon, propertiesPanel, componentData.getDisplayName() + " properties");

        add(tabbedPane);

        revalidate();
        repaint();
    }

    private JBTabbedPane createDefaultTabbedPane(FlowGraph graph) {
        JBTabbedPane tabbedPane = new JBTabbedPane();
        DefaultPropertiesPanel propertiesPanel = new DefaultPropertiesPanel();

        InputField<String> titleField = new StringInputField();
        titleField.setValue(graph.title());
        titleField.addListener(value -> {
            graph.setTitle(value);
            snapshot.onDataChange();
        });

        FormBuilder.get()
                .addLabel("Title", propertiesPanel)
                .addLastField(titleField, propertiesPanel);

        InputField<String> descriptionField = new StringInputField();
        descriptionField.setValue(graph.description());
        descriptionField.addListener(value -> {
            graph.setDescription(value);
            snapshot.onDataChange();
        });

        FormBuilder.get()
                .addLabel("Description", propertiesPanel)
                .addLastField(descriptionField, propertiesPanel);


        JBPanel box = createPropertiesBoxPanel(propertiesPanel);
        tabbedPane.addTab("Flow properties", Icons.FileTypeFlow, box, "flow properties");
        return tabbedPane;
    }

    @Override
    public void onUnselect() {
        SwingUtilities.invokeLater(() -> {
            removeAll();
            JBTabbedPane defaultTabbedPane = createDefaultTabbedPane(snapshot.getGraph());
            add(defaultTabbedPane);
            revalidate();
        });
    }

    private JBPanel createPropertiesPanel(ComponentData componentData, FlowSnapshot snapshot, GraphNode node) {
        JBPanel propertiesPanel = NodePropertiesRendererFactory.get()
                .component(componentData)
                .snapshot(snapshot)
                .module(module)
                .build()
                .render(node);

        JBPanel propertiesBoxContainer = createPropertiesBoxPanel(propertiesPanel);
        return createPropertiesHolder(propertiesBoxContainer);
    }

    private JBPanel createPropertiesHolder(JBPanel propertiesBoxContainer) {
        JBPanel propertiesHolder = new JBPanel();
        propertiesHolder.setLayout(new BorderLayout());
        propertiesHolder.add(propertiesBoxContainer, CENTER);
        return propertiesHolder;
    }

    private JBPanel createPropertiesBoxPanel(JBPanel propertiesListPanel) {
        JBPanel fillerPanel = new JBPanel();
        fillerPanel.add(Box.createGlue());

        JBPanel propertiesBoxContainer = new JBPanel();
        propertiesBoxContainer.setLayout(new BorderLayout());
        propertiesBoxContainer.add(propertiesListPanel, NORTH);
        propertiesBoxContainer.add(fillerPanel, CENTER);
        return propertiesBoxContainer;
    }
}

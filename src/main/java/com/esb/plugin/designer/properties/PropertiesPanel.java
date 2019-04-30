package com.esb.plugin.designer.properties;

import com.esb.plugin.component.Component;
import com.esb.plugin.component.generic.GenericPropertyRenderer;
import com.esb.plugin.designer.SelectListener;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.NothingSelectedNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import javax.swing.border.MatteBorder;

public class PropertiesPanel extends JBPanel implements SelectListener {

    private final Module module;
    private final VirtualFile file;

    public PropertiesPanel(Module module, VirtualFile file) {
        MatteBorder matteBorder = BorderFactory.createMatteBorder(0, 10, 0, 0, getBackground());
        setBorder(matteBorder);
        setBackground(JBColor.WHITE);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.module = module;
        this.file = file;
    }

    @Override
    public void onSelect(FlowGraph graph, GraphNode node) {
        if (node instanceof NothingSelectedNode) return;

        Component component = node.component();

        removeAll();

        add(createTitleLabel(component.getDisplayName()));

        PropertyRenderer renderer = new GenericPropertyRenderer(module, graph, file);
        renderer.render(this, component);

        // Add spacer
        add(Box.createVerticalGlue());

        revalidate();
        repaint();
    }

    @Override
    public void onUnselect(FlowGraph graph, GraphNode node) {
        removeAll();
        revalidate();
        repaint();
    }

    private JLabel createTitleLabel(String title) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setBorder(JBUI.Borders.empty(5, 5, 0, 0));
        return titleLabel;
    }

}

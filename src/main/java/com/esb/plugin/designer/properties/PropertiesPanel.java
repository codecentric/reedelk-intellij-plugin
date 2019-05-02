package com.esb.plugin.designer.properties;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.component.Component;
import com.esb.plugin.designer.SelectListener;
import com.esb.plugin.graph.FlowGraph;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.NothingSelectedNode;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;

import javax.swing.*;
import javax.swing.border.MatteBorder;

public class PropertiesPanel extends JBPanel implements SelectListener {

    private final MatteBorder border = BorderFactory.createMatteBorder(0, 10, 0, 0, getBackground());

    private final Module module;
    private final VirtualFile file;

    public PropertiesPanel(Module module, VirtualFile file) {
        setBorder(border);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.module = module;
        this.file = file;
    }

    @Override
    public void onSelect(FlowGraph graph, GraphNode node) {
        if (node instanceof NothingSelectedNode) return;

        Component component = node.component();

        removeAll();

        JBPanel propertiesPanel = PropertyRendererFactory.get()
                .component(component)
                .module(module)
                .graph(graph)
                .file(file)
                .build()
                .render(node);

        JBTabbedPane tabbedPane = new JBTabbedPane();

        Icon icon = Icons.forComponentAsIcon(component.getFullyQualifiedName());
        tabbedPane.addTab(
                component.getDisplayName(),
                icon,
                propertiesPanel,
                component.getDisplayName() + " properties");

        add(tabbedPane);

        revalidate();
        repaint();
    }

    @Override
    public void onUnselect(FlowGraph graph, GraphNode node) {
        removeAll();
        revalidate();
        repaint();
    }

}

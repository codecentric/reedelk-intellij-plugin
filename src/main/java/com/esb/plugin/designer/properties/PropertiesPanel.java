package com.esb.plugin.designer.properties;

import com.esb.plugin.commons.Icons;
import com.esb.plugin.component.ComponentData;
import com.esb.plugin.designer.SelectListener;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.NothingSelectedNode;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;

import javax.swing.*;
import javax.swing.border.MatteBorder;

public class PropertiesPanel extends JBPanel implements SelectListener {

    private final MatteBorder border = BorderFactory.createMatteBorder(0, 10, 0, 0, getBackground());


    public PropertiesPanel() {
        setBorder(border);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    }

    @Override
    public void onSelect(GraphSnapshot snapshot, GraphNode node) {
        if (node instanceof NothingSelectedNode) return;

        ComponentData componentData = node.component();

        removeAll();

        JBPanel propertiesPanel = PropertyRendererFactory.get()
                .component(componentData)
                .snapshot(snapshot)
                .build()
                .render(node);

        JBTabbedPane tabbedPane = new JBTabbedPane();

        Icon icon = Icons.forComponentAsIcon(componentData.getFullyQualifiedName());
        tabbedPane.addTab(
                componentData.getDisplayName(),
                icon,
                propertiesPanel,
                componentData.getDisplayName() + " properties");

        add(tabbedPane);

        revalidate();
        repaint();
    }

    @Override
    public void onUnselect() {
        removeAll();
        revalidate();
        repaint();
    }

}

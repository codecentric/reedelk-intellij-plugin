package com.esb.plugin.editor.properties;

import com.esb.plugin.editor.designer.SelectListener;
import com.esb.plugin.editor.properties.widget.GraphTabbedPane;
import com.esb.plugin.editor.properties.widget.PropertiesTabbedPane;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.NothingSelectedNode;
import com.intellij.openapi.module.Module;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.AncestorEvent;

public class PropertiesPanel extends JBPanel implements SelectListener {

    private final MatteBorder border = BorderFactory.createMatteBorder(0, 10, 0, 0, getBackground());

    private final Module module;
    private final FlowSnapshot snapshot;
    private final String unselectedTabTitle;
    private final Icon icon;

    public PropertiesPanel(Module module, FlowSnapshot snapshot, String unselectedTabTitle, Icon icon) {
        this.module = module;
        this.snapshot = snapshot;
        this.icon = icon;
        this.unselectedTabTitle = unselectedTabTitle;

        setBorder(border);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setupAncestorListener();

        JBTabbedPane defaultTabbedPane =
                new GraphTabbedPane(icon, unselectedTabTitle, snapshot);
        updateTabbedPane(defaultTabbedPane);
    }

    @Override
    public void onSelect(FlowSnapshot snapshot, GraphNode node) {
        if (node instanceof NothingSelectedNode) {
            JBTabbedPane defaultTabbedPane =
                    new GraphTabbedPane(icon, unselectedTabTitle, snapshot);
            updateTabbedPane(defaultTabbedPane);
        } else {
            PropertiesTabbedPane propertiesTabbedPane =
                    new PropertiesTabbedPane(node, module, snapshot);
            updateTabbedPane(propertiesTabbedPane);
        }
    }

    @Override
    public void onUnselect() {
        JBTabbedPane defaultTabbedPane =
                new GraphTabbedPane(icon, unselectedTabTitle, snapshot);
        updateTabbedPane(defaultTabbedPane);
    }

    private void updateTabbedPane(JBTabbedPane tabbedPane) {
        SwingUtilities.invokeLater(() -> {
            removeAll();
            add(tabbedPane);
            revalidate();
        });
    }

    private void setupAncestorListener() {
        addAncestorListener(new AncestorListenerAdapter() {
            @Override
            public void ancestorRemoved(AncestorEvent event) {
                super.ancestorRemoved(event);
                removeAll();
            }
        });
    }
}

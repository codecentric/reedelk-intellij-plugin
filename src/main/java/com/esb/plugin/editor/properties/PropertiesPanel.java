package com.esb.plugin.editor.properties;

import com.esb.plugin.editor.designer.SelectListener;
import com.esb.plugin.editor.properties.widget.GraphMetadataPane;
import com.esb.plugin.editor.properties.widget.PropertiesTabbedPane;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.NothingSelectedNode;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTabbedPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.AncestorEvent;

public class PropertiesPanel extends JBPanel implements SelectListener {

    private final MatteBorder border =
            BorderFactory.createMatteBorder(0, 10, 0, 0, getBackground());

    private final String unselectedTabTitle;
    private final Module module;
    private final Icon icon;

    private Disposable currentPane;

    public PropertiesPanel(@NotNull Module module,
                           @NotNull FlowSnapshot snapshot,
                           @NotNull String unselectedTabTitle,
                           @NotNull Icon icon) {
        this.icon = icon;
        this.module = module;
        this.unselectedTabTitle = unselectedTabTitle;

        setBorder(border);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setupAncestorListener();

        // By default just show info about the graph
        GraphMetadataPane defaultTabbedPane =
                new GraphMetadataPane(icon, unselectedTabTitle, snapshot);
        updateTabbedPane(defaultTabbedPane);

        this.currentPane = defaultTabbedPane;
    }

    @Override
    public void onSelect(FlowSnapshot snapshot, GraphNode selected) {
        if (selected instanceof NothingSelectedNode) {
            // If nothing is selected we display input fields to edit graph metadata,
            // such as title and description.
            GraphMetadataPane graphProperties =
                    new GraphMetadataPane(icon, unselectedTabTitle, snapshot);
            updateTabbedPane(graphProperties);

            this.currentPane = graphProperties;

        } else {
            // Otherwise the properties panel displays the properties
            // of the component currently selected.
            PropertiesTabbedPane nodeProperties =
                    new PropertiesTabbedPane(selected, module, snapshot);
            updateTabbedPane(nodeProperties);

            this.currentPane = nodeProperties;
        }
    }

    @Override
    public void onUnselect() {
        if (currentPane != null) {
            currentPane.dispose();
        }
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

package com.esb.plugin.editor.properties;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.designer.ComponentSelectedListener;
import com.esb.plugin.editor.properties.widget.ContainerFactory;
import com.esb.plugin.editor.properties.widget.DisposableScrollPane;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.NothingSelectedNode;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.ui.components.JBTabbedPane;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.AncestorEvent;
import java.awt.*;

public class PropertiesPanel extends PropertiesBasePanel implements ComponentSelectedListener {

    private final MatteBorder border =
            BorderFactory.createMatteBorder(10, 10, 0, 0, Color.WHITE);
    private final Project project;

    private Disposable currentPane;

    public PropertiesPanel(@NotNull Project project) {
        setBorder(border);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setupAncestorListener();
        this.project = project;

        project.getMessageBus()
                .connect().subscribe(ComponentSelectedListener.COMPONENT_SELECTED_TOPIC, this);
    }

    @Override
    public void onComponentUnSelected() {
        if (currentPane != null) {
            Disposer.dispose(currentPane);
        }
    }

    @Override
    public void onComponentSelected(Module module, FlowSnapshot snapshot, GraphNode selected) {
        if (selected instanceof NothingSelectedNode) {
            // If nothing is selected we display input fields to edit graph metadata,
            // such as title and description.
            //  GraphMetadataPane graphProperties =
            //        new GraphMetadataPane(icon, unselectedTabTitle, snapshot);
            //updateTabbedPane(graphProperties);

            //this.currentPane = graphProperties;

        } else {
            // Otherwise the properties panel displays the properties
            // of the component currently selected.


            ComponentData componentData = selected.componentData();


            DisposableScrollPane propertiesPanel =
                    ContainerFactory.createPropertiesPanel(module, componentData, snapshot, selected);


            Icon icon = componentData.getComponentIcon();
            String displayName = componentData.getDisplayName();


            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(PropertiesPanelToolWindowFactory.ID);
            toolWindow.setStripeTitle(displayName);
            toolWindow.setTitle(displayName);
            toolWindow.setIcon(icon);

            SwingUtilities.invokeLater(() -> {
                removeAll();
                add(propertiesPanel);
                revalidate();
            });

            this.currentPane = propertiesPanel;
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

package com.esb.plugin.editor.properties;

import com.esb.plugin.commons.Colors;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.designer.ComponentSelectedListener;
import com.esb.plugin.editor.properties.widget.ContainerFactory;
import com.esb.plugin.editor.properties.widget.DisposableScrollPane;
import com.esb.plugin.editor.properties.widget.GraphMetadataPane;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;
import com.esb.plugin.graph.node.NothingSelectedNode;
import com.esb.plugin.service.project.SelectedComponentManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.AncestorListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.AncestorEvent;

public class PropertiesPanel extends PropertiesBasePanel implements ComponentSelectedListener {

    private final MatteBorder border =
            BorderFactory.createMatteBorder(10, 10, 0, 0, Colors.PROPERTIES_BACKGROUND);
    private final Project project;

    private Disposable currentPane;
    private SelectedComponentManager selectedComponentManager;

    public PropertiesPanel(@NotNull Project project) {
        setBorder(border);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setupAncestorListener();
        this.project = project;

        selectedComponentManager = ServiceManager.getService(project, SelectedComponentManager.class);

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

        ToolWindow toolWindow = ToolWindowManager
                .getInstance(project)
                .getToolWindow(PropertiesPanelToolWindowFactory.ID);


        if (selected instanceof NothingSelectedNode) {
            // If nothing is selected we display input fields to edit graph metadata,
            // such as title and description.
            GraphMetadataPane graphProperties = new GraphMetadataPane(snapshot);
            toolWindow.setTitle("Flow");

            SwingUtilities.invokeLater(() -> {
                removeAll();
                add(graphProperties);
                revalidate();
            });

            this.currentPane = graphProperties;

        } else {

            // Otherwise the properties panel displays the properties
            // of the component currently selected.
            ComponentData componentData = selected.componentData();


            DisposableScrollPane propertiesPanel =
                    ContainerFactory.createPropertiesPanel(module, componentData, snapshot, selected);


            Icon icon = componentData.getComponentIcon();
            String displayName = componentData.getDisplayName();
            toolWindow.setTitle(displayName);

            SwingUtilities.invokeLater(() -> {
                removeAll();
                add(propertiesPanel);
                revalidate();
            });

            this.currentPane = propertiesPanel;
        }
    }

    private void setupAncestorListener() {
        addAncestorListener(new AncestorListenerAdapter() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                super.ancestorAdded(event);
                selectedComponentManager.getCurrentSelection()
                        .ifPresent(currentSelection -> onComponentSelected(
                                currentSelection.getModule(),
                                currentSelection.getSnapshot(),
                                currentSelection.getSelectedNode()));
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                super.ancestorRemoved(event);
                removeAll();
            }
        });
    }
}

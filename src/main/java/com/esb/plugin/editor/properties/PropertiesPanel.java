package com.esb.plugin.editor.properties;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.properties.widget.*;
import com.esb.plugin.service.project.DesignerSelectionManager;
import com.esb.plugin.service.project.SelectableItem;
import com.esb.plugin.service.project.SelectableItemComponent;
import com.esb.plugin.service.project.SelectableItemFlow;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.AncestorEvent;

import static com.esb.plugin.service.project.DesignerSelectionManager.CurrentSelectionListener;

public class PropertiesPanel extends PropertiesBasePanel implements CurrentSelectionListener {

    private final Project project;

    private Disposable currentPane;
    private DesignerSelectionManager designerSelectionManager;
    private SelectableItem currentSelection;

    PropertiesPanel(@NotNull Project project) {
        setBorder(JBUI.Borders.empty());
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setupAncestorListener();
        this.project = project;

        designerSelectionManager = ServiceManager.getService(project, DesignerSelectionManager.class);

        project.getMessageBus()
                .connect().subscribe(CurrentSelectionListener.CURRENT_SELECTION_TOPIC, this);
    }

    @Override
    public void onSelection(SelectableItem selectedItem) {
        this.currentSelection = selectedItem;

        ToolWindow toolWindow = getToolWindow();

        if (selectedItem instanceof SelectableItemComponent) {
            // Otherwise the properties panel displays the properties
            // of the component currently selected.
            ComponentData componentData = selectedItem.getSelectedNode().componentData();


            DisposableScrollPane propertiesPanel =
                    ContainerFactory.createPropertiesPanel(selectedItem.getModule(),
                            componentData,
                            selectedItem.getSnapshot(),
                            selectedItem.getSelectedNode());

            String displayName = componentData.getDisplayName();
            toolWindow.setTitle(displayName);

            SwingUtilities.invokeLater(() -> {
                removeAll();
                add(propertiesPanel);
                revalidate();
            });

            this.currentPane = propertiesPanel;
        }


        if (selectedItem instanceof SelectableItemFlow) {
            // If nothing is selected we display input fields to edit graph metadata,
            // such as title and description.
            FlowMetadataPanel graphProperties = new FlowMetadataPanel(selectedItem.getSnapshot());
            toolWindow.setTitle("Flow");

            SwingUtilities.invokeLater(() -> {
                removeAll();
                add(graphProperties);
                revalidate();
            });

            this.currentPane = graphProperties;
        }
    }

    @Override
    public void onUnSelected(SelectableItem unselected) {
        if (currentSelection == unselected) {
            if (currentPane != null) {
                Disposer.dispose(currentPane);
            }

            DisposablePanel empty = new EmptySelectionPanel(project);
            SwingUtilities.invokeLater(() -> {
                removeAll();
                add(empty);
                revalidate();
            });

            this.currentPane = empty;
        }
    }

    private void setupAncestorListener() {
        addAncestorListener(new AncestorListenerAdapter() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                super.ancestorAdded(event);
                designerSelectionManager.getCurrentSelection()
                        .ifPresent(PropertiesPanel.this::onSelection);
            }
        });
    }

    private ToolWindow getToolWindow() {
        return ToolWindowManager
                .getInstance(project)
                .getToolWindow(PropertiesPanelToolWindowFactory.ID);
    }
}

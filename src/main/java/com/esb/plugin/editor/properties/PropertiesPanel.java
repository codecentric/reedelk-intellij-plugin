package com.esb.plugin.editor.properties;

import com.esb.plugin.commons.Labels;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.properties.widget.*;
import com.esb.plugin.service.project.*;
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
        this.designerSelectionManager =
                ServiceManager.getService(project, DesignerSelectionManager.class);

        setEmptySelection();

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

            updateContent(propertiesPanel);
            this.currentPane = propertiesPanel;

        } else if (selectedItem instanceof SelectableItemFlow ||
                selectedItem instanceof SelectableItemSubflow) {

            FlowAndSubflowMetadataPanel panel = new FlowAndSubflowMetadataPanel(selectedItem.getSnapshot());

            String toolWindowTitle = selectedItem instanceof SelectableItemFlow ?
                    Labels.PROPERTIES_PANEL_FLOW_TITLE :
                    Labels.PROPERTIES_PANEL_SUBFLOW_TITLE;
            toolWindow.setTitle(toolWindowTitle);


            updateContent(panel);
            this.currentPane = panel;

        } else {
            throw new IllegalStateException("Unknown selectable item");
        }
    }

    @Override
    public void onUnSelected(SelectableItem unselected) {
        if (currentSelection == unselected) {
            if (currentPane != null) {
                Disposer.dispose(currentPane);
            }
            setEmptySelection();
        }
    }

    private void setEmptySelection() {
        DisposablePanel empty = new EmptySelectionPanel(project);
        updateContent(empty);
        this.currentPane = empty;
    }

    private void updateContent(JComponent content) {
        SwingUtilities.invokeLater(() -> {
            removeAll();
            add(content);
            revalidate();
        });
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

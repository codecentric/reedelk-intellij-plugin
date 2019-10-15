package com.reedelk.plugin.editor.properties;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.commons.Labels;
import com.reedelk.plugin.commons.ToolWindowUtils;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.editor.properties.widget.*;
import com.reedelk.plugin.service.project.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.AncestorEvent;

import static com.reedelk.plugin.service.project.DesignerSelectionManager.CurrentSelectionListener;

public class PropertiesPanel extends DisposablePanel implements CurrentSelectionListener {

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
        // We must Dispose the current content before
        // creating and assigning a new content.
        if (currentPane != null) {
            Disposer.dispose(currentPane);
        }

        this.currentSelection = selectedItem;

        // We display the tool window if an item is selected
        // and it is still not visible yet.
        ToolWindowUtils.ComponentProperties.show(project);

        if (selectedItem instanceof SelectableItemComponent) {
            createFlowComponentContent(selectedItem);
        } else if (selectedItem instanceof SelectableItemFlow || selectedItem instanceof SelectableItemSubflow) {
            createFlowOrSubflowContent(selectedItem);
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
            currentSelection = null;
        }
    }

    @Override
    public void refresh() {
        if (currentSelection != null) {
            onSelection(currentSelection);
        }
    }

    private void createFlowOrSubflowContent(SelectableItem selectedItem) {
        FlowAndSubflowMetadataPanel panel = new FlowAndSubflowMetadataPanel(selectedItem.getSnapshot());

        String toolWindowTitle = selectedItem instanceof SelectableItemFlow ?
                Labels.PROPERTIES_PANEL_FLOW_TITLE :
                Labels.PROPERTIES_PANEL_SUBFLOW_TITLE;
        setToolWindowTitle(toolWindowTitle);

        updateContent(panel);
        this.currentPane = panel;
    }

    private void createFlowComponentContent(SelectableItem selectedItem) {
        // Otherwise the properties panel displays the properties
        // of the component currently selected.
        ComponentData componentData = selectedItem.getSelectedNode().componentData();

        DisposableScrollPane propertiesPanel =
                ContainerFactory.createPropertiesPanel(selectedItem.getModule(),
                        componentData,
                        selectedItem.getSnapshot(),
                        selectedItem.getSelectedNode());

        String displayName = componentData.getDisplayName();
        setToolWindowTitle(displayName);

        updateContent(propertiesPanel);
        this.currentPane = propertiesPanel;
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
                designerSelectionManager.getCurrentSelection()
                        .ifPresent(PropertiesPanel.this::onSelection);
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                // Properties panel is collapsed, we need to clear
                // the current content if present.
                if (currentPane != null) {
                    Disposer.dispose(currentPane);
                    SwingUtilities.invokeLater(() -> removeAll());
                }
            }
        });
    }

    private void setToolWindowTitle(String newToolWindowTitle) {
        ToolWindowUtils.ComponentProperties.get(project)
                .setTitle(newToolWindowTitle);
    }
}
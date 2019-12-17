package com.reedelk.plugin.editor.properties;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.commons.ToolWindowUtils;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.editor.DesignerEditor;
import com.reedelk.plugin.editor.properties.commons.*;
import com.reedelk.plugin.editor.properties.renderer.ComponentPropertiesRendererFactory;
import com.reedelk.plugin.editor.properties.selection.*;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.topic.ReedelkTopics;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class PropertiesPanel extends DisposablePanel implements SelectionChangeListener, FileEditorManagerListener {

    private final transient Project project;
    private transient Disposable currentPane;
    private transient MessageBusConnection busConnection;
    private SelectableItem currentSelection;

    PropertiesPanel(@NotNull Project project) {
        setBorder(JBUI.Borders.empty());
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        this.project = project;

        setEmptySelection();

        busConnection = project.getMessageBus().connect();
        busConnection.subscribe(ReedelkTopics.CURRENT_COMPONENT_SELECTION_EVENTS, this);
        busConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this);
    }

    @Override
    public void onSelection(SelectableItem selectedItem) {

        if (currentSelection == selectedItem) {
            // Nothing to do
            return;
        }

        // We must Dispose the current content before
        // creating and assigning a new content.
        DisposableUtils.dispose(currentPane);

        this.currentSelection = selectedItem;

        if (selectedItem instanceof SelectableItemComponent) {
            createFlowComponentContent(selectedItem);
        } else if (selectedItem instanceof SelectableItemFlow) {
            createFlowOrSubflowContent(selectedItem);
        } else if (selectedItem instanceof SelectableItemSubflow) {
            createFlowOrSubflowContent(selectedItem);
        }
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        if (!(event.getNewEditor() instanceof DesignerEditor)) {
            DisposableUtils.dispose(currentPane);
            setEmptySelection();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        busConnection.disconnect();
        DisposableUtils.dispose(currentPane);
    }

    private void createFlowOrSubflowContent(SelectableItem selectedItem) {
        FlowAndSubflowMetadataPanel panel = new FlowAndSubflowMetadataPanel(selectedItem.getSnapshot());

        String toolWindowTitle = selectedItem instanceof SelectableItemFlow ?
                message("properties.panel.flow.title") :
                message("properties.panel.subflow.title");
        setToolWindowTitle(toolWindowTitle);

        updateContent(panel);
        this.currentPane = panel;
    }

    private void createFlowComponentContent(SelectableItem selectedItem) {
        // Otherwise the properties panel displays the properties
        // of the component currently selected.
        ComponentData componentData = selectedItem.getSelectedNode().componentData();

        DisposableScrollPane propertiesPanel = createPropertiesPanel(selectedItem.getModule(),
                componentData,
                selectedItem.getSnapshot(),
                selectedItem.getSelectedNode());
        String displayName = componentData.getDisplayName();
        setToolWindowTitle(displayName);

        updateContent(propertiesPanel);
        currentPane = propertiesPanel;
    }

    private DisposableScrollPane createPropertiesPanel(Module module, ComponentData componentData, FlowSnapshot snapshot, GraphNode node) {
        DisposablePanel propertiesPanel = ComponentPropertiesRendererFactory.get()
                .component(componentData)
                .snapshot(snapshot)
                .module(module)
                .build()
                .render(node);
        DisposablePanel propertiesBoxContainer = ContainerFactory.pushTop(propertiesPanel);
        propertiesBoxContainer.setBorder(JBUI.Borders.empty(10));
        return ContainerFactory.makeItScrollable(propertiesBoxContainer);
    }

    private void setEmptySelection() {
        currentSelection = null;
        DisposablePanel empty = new EmptySelectionPanel(project);
        updateContent(empty);
        currentPane = empty;
    }

    private void updateContent(JComponent content) {
        removeAll();
        add(content);
        revalidate();
    }

    private void setToolWindowTitle(String newToolWindowTitle) {
        ToolWindowUtils.setPropertiesPanelToolWindowTitle(project, newToolWindowTitle);
    }
}

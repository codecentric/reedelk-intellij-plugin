package com.reedelk.plugin.editor.properties;

import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.commons.ToolWindowUtils;
import com.reedelk.plugin.commons.Topics;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.editor.DesignerEditor;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.EmptySelectionPanel;
import com.reedelk.plugin.editor.properties.commons.FlowAndSubflowMetadataPanel;
import com.reedelk.plugin.editor.properties.renderer.ComponentPropertiesRendererFactory;
import com.reedelk.plugin.editor.properties.selection.*;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class PropertiesPanel extends DisposablePanel implements SelectionChangeListener, FileEditorManagerListener {

    private final transient Project project;
    private transient JComponent currentPane;
    private transient MessageBusConnection busConnection;
    private transient SelectableItem currentSelection;

    PropertiesPanel(@NotNull Project project) {
        setBorder(JBUI.Borders.empty());
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        this.project = project;

        setEmptySelection();

        busConnection = project.getMessageBus().connect();
        busConnection.subscribe(Topics.CURRENT_COMPONENT_SELECTION_EVENTS, this);
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
    public void unselect() {
        setEmptySelection();
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        if (!(event.getNewEditor() instanceof DesignerEditor)) {
            DisposableUtils.dispose(currentPane);
            setEmptySelection();
        }
    }

    @Override
    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        FileEditor[] allEditors = FileEditorManager.getInstance(project).getAllEditors();
        if (allEditors.length == 0) {
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

        JComponent propertiesPanel = createPropertiesPanel(selectedItem.getModule(),
                componentData,
                selectedItem.getSnapshot(),
                selectedItem.getSelectedNode());
        String displayName = componentData.getDisplayName();
        setToolWindowTitle(displayName);

        updateContent(propertiesPanel);
        currentPane = propertiesPanel;
    }

    private JComponent createPropertiesPanel(Module module, ComponentData componentData, FlowSnapshot snapshot, GraphNode node) {
        return ComponentPropertiesRendererFactory.get()
                .component(componentData)
                .snapshot(snapshot)
                .module(module)
                .build()
                .render(node);
    }

    private void setEmptySelection() {
        currentSelection = null;
        DisposableUtils.dispose(currentPane);
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

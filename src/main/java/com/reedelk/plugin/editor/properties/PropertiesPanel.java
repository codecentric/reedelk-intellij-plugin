package com.reedelk.plugin.editor.properties;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.commons.ToolWindowUtils;
import com.reedelk.plugin.component.domain.ComponentData;
import com.reedelk.plugin.editor.properties.commons.*;
import com.reedelk.plugin.editor.properties.renderer.ComponentPropertiesRendererFactory;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.plugin.service.project.DesignerSelectionManager;
import com.reedelk.plugin.service.project.impl.SelectableItem;
import com.reedelk.plugin.service.project.impl.SelectableItemComponent;
import com.reedelk.plugin.service.project.impl.SelectableItemFlow;
import com.reedelk.plugin.service.project.impl.SelectableItemSubflow;
import com.reedelk.plugin.topic.ReedelkTopics;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.AncestorEvent;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.plugin.service.project.DesignerSelectionManager.CurrentSelectionListener;

public class PropertiesPanel extends DisposablePanel implements CurrentSelectionListener {

    private final transient Project project;

    private transient Disposable currentPane;
    private transient SelectableItem currentSelection;
    private transient MessageBusConnection busConnection;
    private transient DesignerSelectionManager designerSelectionManager;

    PropertiesPanel(@NotNull Project project) {
        setBorder(JBUI.Borders.empty());
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setupAncestorListener();

        this.project = project;
        this.designerSelectionManager = DesignerSelectionManager.getInstance(project);

        setEmptySelection();

        busConnection = project.getMessageBus().connect();
        busConnection.subscribe(ReedelkTopics.CURRENT_COMPONENT_SELECTION_EVENTS, this);
    }

    @Override
    public void onSelection(SelectableItem selectedItem) {
        // We must Dispose the current content before
        // creating and assigning a new content.
        DisposableUtils.dispose(currentPane);

        this.currentSelection = selectedItem;

        // We display the tool window if an item is selected
        // and it is still not visible yet.
        ToolWindowUtils.show(project);

        if (selectedItem instanceof SelectableItemComponent) {
            createFlowComponentContent(selectedItem);
        } else if (selectedItem instanceof SelectableItemFlow || selectedItem instanceof SelectableItemSubflow) {
            createFlowOrSubflowContent(selectedItem);
        }
    }

    @Override
    public void onUnSelected(SelectableItem unselected) {
        if (currentSelection == unselected) {
            makeCurrentPaneInvisible();
            DisposableUtils.dispose(currentPane);
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

    @Override
    public void dispose() {
        super.dispose();
        busConnection.disconnect();
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
        this.currentPane = propertiesPanel;
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
                DisposableUtils.dispose(currentPane);
                SwingUtilities.invokeLater(PropertiesPanel.this::removeAll);
            }
        });
    }

    private void setToolWindowTitle(String newToolWindowTitle) {
        ToolWindowUtils.get(project).setTitle(newToolWindowTitle);
    }

    // Before disposing it we hide the panel so that all the properties
    // with an editor in it do not 'flicker' when the editor is being
    // disposed.
    private void makeCurrentPaneInvisible() {
        if (currentPane instanceof JComponent) {
            ((JComponent) currentPane).setVisible(false);
        }
    }
}

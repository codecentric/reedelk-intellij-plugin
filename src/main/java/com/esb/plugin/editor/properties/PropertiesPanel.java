package com.esb.plugin.editor.properties;

import com.esb.plugin.commons.Colors;
import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.editor.properties.widget.ContainerFactory;
import com.esb.plugin.editor.properties.widget.DisposableScrollPane;
import com.esb.plugin.service.project.DesignerSelectionManager;
import com.esb.plugin.service.project.SelectableItem;
import com.esb.plugin.service.project.SelectableItemComponent;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.AncestorListenerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.event.AncestorEvent;

import static com.esb.plugin.service.project.DesignerSelectionManager.CurrentSelectionListener;

public class PropertiesPanel extends PropertiesBasePanel implements CurrentSelectionListener {

    private final MatteBorder border =
            BorderFactory.createMatteBorder(10, 10, 0, 0, Colors.PROPERTIES_BACKGROUND);
    private final Project project;

    private Disposable currentPane;
    private DesignerSelectionManager designerSelectionManager;

    public PropertiesPanel(@NotNull Project project) {
        setBorder(border);
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setupAncestorListener();
        this.project = project;

        designerSelectionManager = ServiceManager.getService(project, DesignerSelectionManager.class);

        project.getMessageBus()
                .connect().subscribe(CurrentSelectionListener.CURRENT_SELECTION_TOPIC, this);
    }

    @Override
    public void onSelection(SelectableItem selectedItem) {
        ToolWindow toolWindow = ToolWindowManager
                .getInstance(project)
                .getToolWindow(PropertiesPanelToolWindowFactory.ID);

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


        /**
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

         } */
    }

    @Override
    public void onUnSelected() {
        if (currentPane != null) {
            Disposer.dispose(currentPane);
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

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                super.ancestorRemoved(event);
                removeAll();
            }
        });
    }
}

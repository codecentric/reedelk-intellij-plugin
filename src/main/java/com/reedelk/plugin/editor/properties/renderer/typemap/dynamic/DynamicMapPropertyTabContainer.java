package com.reedelk.plugin.editor.properties.renderer.typemap.dynamic;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.properties.commons.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;

import static com.intellij.icons.AllIcons.General.Add;
import static com.intellij.icons.AllIcons.General.Remove;
import static com.reedelk.plugin.message.ReedelkBundle.message;

class DynamicMapPropertyTabContainer extends DisposablePanel {

    private boolean loaded = false;

    DynamicMapPropertyTabContainer(@NotNull Module module,
                                   @NotNull DisposableTableModel tableModel,
                                   @NotNull DisposableTableColumnModelFactory columnModelFactory) {
        setLayout(new BorderLayout());
        add(new LoadingContentPanel());

        addComponentListener(new ComponentListenerAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                // If the content was already loaded, there is no need to loading it again.
                if (loaded) return;

                // Lazy loading of Table content. We do it so that we can optimize the rendering
                // of the component's properties.
                SwingUtilities.invokeLater(() -> {
                    DisposablePanel mapContainer = new DisposablePanel();
                    mapContainer.setLayout(new BoxLayout(mapContainer, BoxLayout.Y_AXIS));
                    DisposableTable disposablePropertyTable = new DisposableTable(module.getProject(), tableModel, columnModelFactory);
                    JPanel actionPanel = new TableActionPanel(disposablePropertyTable);
                    mapContainer.add(actionPanel);
                    mapContainer.add(disposablePropertyTable);

                    loaded = true;

                    removeAll();
                    add(mapContainer, BorderLayout.CENTER);
                });
            }
        });
    }

    static class TableActionPanel extends DisposablePanel {
        TableActionPanel(DisposableTable table) {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            add(new ClickableLabel(message("properties.type.map.table.add"), Add, table::addEmptyRow));
            add(new ClickableLabel(message("properties.type.map.table.remove"), Remove, table::removeSelectedRow));
        }
    }
}

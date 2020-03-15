package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.table.JBTable;
import com.reedelk.plugin.editor.properties.commons.*;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ComponentEvent;

import static com.intellij.icons.AllIcons.General.Add;
import static com.intellij.icons.AllIcons.General.Remove;
import static com.reedelk.plugin.message.ReedelkBundle.message;

class DynamicMapPropertyTabContainer extends DisposablePanel {

    private static final String[] COLUMN_NAMES = {"Key", "Value"};

    private boolean loaded = false;

    DynamicMapPropertyTabContainer(Module module, DisposableTableModel tableModel, ContainerContext context) {
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
                    MapTableColumnModel columnModel = new MapTableColumnModel(module, context);
                    DisposableTable disposablePropertyTable =
                            new DisposableTable(module.getProject(), tableModel, columnModel);
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

    static class MapTableColumnModel implements DisposableTableColumnModelFactory {

        private final Module module;
        private final ContainerContext context;

        MapTableColumnModel(Module  module, ContainerContext context) {
            this.module = module;
            this.context = context;
        }

        @Override
        public void create(JBTable table) {
            TableDynamicCellEditor cellEditor = new TableDynamicCellEditor(module, context);
            TableDynamicCellRenderer cellRenderer = new TableDynamicCellRenderer(module, context);

            // Column 1 (the map key)
            TableColumn keyColumn = table.getColumnModel().getColumn(0);
            keyColumn.setHeaderValue(COLUMN_NAMES[0]);
            keyColumn.setCellEditor(new DefaultCellEditor(new JBTextField()));

            // Column 2 (the map value)
            TableColumn valueColumn = table.getColumnModel().getColumn(1);
            valueColumn.setHeaderValue(COLUMN_NAMES[1]);
            valueColumn.setCellRenderer(cellRenderer);
            valueColumn.setCellEditor(cellEditor);
        }
    }
}

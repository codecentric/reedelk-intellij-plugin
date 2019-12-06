package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.commons.TableColumnModelUtils;
import com.reedelk.plugin.editor.properties.CommitPropertiesListener;
import com.reedelk.plugin.editor.properties.commons.*;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.intellij.icons.AllIcons.General.Add;
import static com.intellij.icons.AllIcons.General.Remove;
import static com.reedelk.plugin.message.ReedelkBundle.message;

class DynamicMapPropertyTabContainer extends DisposablePanel {

    private static final String[] COLUMN_NAMES = {"Key", "Value"};

    DynamicMapPropertyTabContainer(Module module, PropertyTable.PropertyTableModel tableModel) {
        MapTableColumnModel columnModel = new MapTableColumnModel(module);
        PropertyTable propertyTable = new PropertyTable(tableModel, columnModel);

        JPanel actionPanel = new TableActionPanel(propertyTable);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(actionPanel);
        add(propertyTable);
    }

    class TableActionPanel extends DisposablePanel {
        TableActionPanel(PropertyTable table) {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            add(new ClickableLabel(message("properties.type.map.table.add"), Add, table::addEmptyRow));
            add(new ClickableLabel(message("properties.type.map.table.remove"), Remove, table::removeSelectedRow));
        }
    }

    class MapTableColumnModel extends DefaultTableColumnModel implements Disposable, CommitPropertiesListener {

        private transient  MessageBusConnection busConnection;
        private transient TableDynamicCellEditor cellEditor;
        private transient TableDynamicCellRenderer cellRenderer;

        MapTableColumnModel(Module  module) {
            this.cellEditor = new TableDynamicCellEditor(module);
            this.cellRenderer = new TableDynamicCellRenderer(module);

            // Column 1 (the map key)
            TableColumn keyColumn = new TableColumn(0);
            keyColumn.setHeaderValue(COLUMN_NAMES[0]);
            keyColumn.setCellEditor(new DefaultCellEditor(new JBTextField()));
            addColumn(keyColumn);

            // Column 2 (the map value)
            TableColumn valueColumn = new TableColumn(1);
            valueColumn.setHeaderValue(COLUMN_NAMES[1]);
            valueColumn.setCellRenderer(cellRenderer);
            valueColumn.setCellEditor(cellEditor);
            addColumn(valueColumn);

            busConnection = module.getProject().getMessageBus().connect();
            busConnection.subscribe(CommitPropertiesListener.COMMIT_TOPIC, this);
        }

        @Override
        public void dispose() {
            busConnection.disconnect();
            DisposableUtils.dispose(cellRenderer);
            DisposableUtils.dispose(cellEditor);
        }

        @Override
        public void onCommit() {
            TableColumnModelUtils.onCommit(this);
        }
    }
}

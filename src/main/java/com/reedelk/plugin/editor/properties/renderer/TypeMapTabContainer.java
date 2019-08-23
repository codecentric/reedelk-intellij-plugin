package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.ui.components.JBPanel;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.widget.ClickableLabel;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import com.reedelk.plugin.editor.properties.widget.PropertyTable;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.reedelk.plugin.commons.Icons.MapType.AddItem;
import static com.reedelk.plugin.commons.Icons.MapType.RemoveItem;
import static com.reedelk.plugin.commons.Labels.ACTION_MAP_TABLE_ADD;
import static com.reedelk.plugin.commons.Labels.ACTION_MAP_TABLE_REMOVE;

class TypeMapTabContainer extends JBPanel {

    private static final String[] COLUMN_NAMES = {"Key", "Value"};

    TypeMapTabContainer(PropertyAccessor propertyAccessor) {
        MapTableColumnModel columnModel = new MapTableColumnModel();
        MapAwareTableModel tableModel = new MapAwareTableModel(propertyAccessor);
        PropertyTable propertyTable = new PropertyTable(tableModel, columnModel);

        JPanel actionPanel = new TableActionPanel(propertyTable);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(actionPanel);
        add(propertyTable);
    }

    class TableActionPanel extends DisposablePanel {
        TableActionPanel(PropertyTable table) {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            add(new ClickableLabel(ACTION_MAP_TABLE_ADD, AddItem, AddItem, table::addEmptyRow));
            add(new ClickableLabel(ACTION_MAP_TABLE_REMOVE, RemoveItem, RemoveItem, table::removeSelectedRow));
        }
    }

    class MapAwareTableModel extends DefaultTableModel implements PropertyTable.PropertyTableModel {

        private final PropertyAccessor accessor;

        MapAwareTableModel(PropertyAccessor accessor) {
            super(0, COLUMN_NAMES.length);
            this.accessor = accessor;
            initialize();
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            super.setValueAt(aValue, row, column);
            updateAccessorData();
        }

        @Override
        public void addRow(Object[] rowData) {
            super.addRow(rowData);
            updateAccessorData();
        }

        @Override
        public void removeRow(int row) {
            super.removeRow(row);
            updateAccessorData();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        private void updateAccessorData() {
            Map<String, String> updated = new LinkedHashMap<>();
            getDataVector().forEach(vector -> {
                String key = (String) (vector.get(0) == null ? "" : vector.get(0));
                String value = (String) (vector.get(1) == null ? "" : vector.get(1));
                updated.put(key, value);
            });
            accessor.set(updated);
        }

        private void initialize() {
            Map<String, ?> map = this.accessor.get();
            if (map != null) {
                map.forEach((BiConsumer<String, Object>) (key, value) -> addRow(new Object[]{key, value}));
            }
        }
    }

    class MapTableColumnModel extends DefaultTableColumnModel {
        MapTableColumnModel() {
            // Column 1 (the map key)
            TableColumn keyColumn = new TableColumn(0);
            keyColumn.setHeaderValue(COLUMN_NAMES[0]);
            addColumn(keyColumn);

            // Column 2 (the map value)
            TableColumn valueColumn = new TableColumn(1);
            valueColumn.setHeaderValue(COLUMN_NAMES[1]);
            addColumn(valueColumn);
        }
    }
}

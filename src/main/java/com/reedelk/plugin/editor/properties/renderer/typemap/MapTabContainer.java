package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.ui.components.JBPanel;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.PropertyTable;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;
import java.util.function.Consumer;

import static com.reedelk.plugin.commons.Icons.MapType.AddItem;
import static com.reedelk.plugin.commons.Icons.MapType.RemoveItem;
import static com.reedelk.plugin.commons.Labels.ACTION_MAP_TABLE_ADD;
import static com.reedelk.plugin.commons.Labels.ACTION_MAP_TABLE_REMOVE;

class MapTabContainer extends JBPanel {

    private static final String[] COLUMN_NAMES = {"Key", "Value"};

    MapTabContainer(PropertyAccessor propertyAccessor) {
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
            getDataVector().forEach(new VectorDataConsumer(updated));
            accessor.set(updated);
        }

        private void initialize() {
            Map<String, String> map = this.accessor.get();
            if (map != null) {
                map.forEach((key, value) -> addRow(new Object[]{key, value}));
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

    class VectorDataConsumer implements Consumer<Vector> {

        private final Map<String, String> map;

        VectorDataConsumer(Map<String, String> map) {
            this.map = map;
        }

        @Override
        public void accept(Vector vector) {
            String key = getOrEmptyIfNull(vector, 0); // 0 is the key
            String value = getOrEmptyIfNull(vector, 1); // 1 is the value
            map.put(key, value);
        }

        private String getOrEmptyIfNull(Vector vector, int index) {
            String value = (String) vector.get(index);
            return value == null ? "" : value;
        }
    }
}

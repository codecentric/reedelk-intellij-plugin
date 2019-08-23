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

import static com.reedelk.plugin.commons.Icons.MapType.AddItem;
import static com.reedelk.plugin.commons.Icons.MapType.RemoveItem;
import static com.reedelk.plugin.commons.Labels.ACTION_MAP_TABLE_ADD;
import static com.reedelk.plugin.commons.Labels.ACTION_MAP_TABLE_REMOVE;

public class TypeMapTabContainer extends JBPanel {

    private static final String[] COLUMN_NAMES = {"Key", "Value"};

    public TypeMapTabContainer(PropertyAccessor propertyAccessor) {
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
        }

        @Override
        public void addRow(Object[] rowData) {
            super.addRow(rowData);
        }

        @Override
        public void removeRow(int row) {
            super.removeRow(row);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }
    }

    class MapTableColumnModel extends DefaultTableColumnModel {
        MapTableColumnModel() {
            TableColumn keyColumn = new TableColumn(0);
            keyColumn.setHeaderValue(COLUMN_NAMES[0]);
            addColumn(keyColumn);

            TableColumn valueColumn = new TableColumn(1);
            valueColumn.setHeaderValue(COLUMN_NAMES[1]);
            addColumn(valueColumn);
        }
    }
}

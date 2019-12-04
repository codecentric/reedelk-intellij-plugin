package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.commons.VectorUtils;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.PropertyTable;
import com.reedelk.plugin.editor.properties.renderer.commons.ScriptEditor;
import com.reedelk.plugin.editor.properties.renderer.typedynamicvalue.DynamicValueScriptEditor;
import com.reedelk.runtime.api.commons.ScriptUtils;
import com.reedelk.runtime.api.commons.StringUtils;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.*;
import java.awt.*;
import java.util.EventObject;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import static com.reedelk.plugin.commons.Icons.MapType.AddItem;
import static com.reedelk.plugin.commons.Icons.MapType.RemoveItem;
import static com.reedelk.plugin.message.ReedelkBundle.message;

class DynamicMapPropertyTabContainer extends DisposablePanel {

    private static final String[] COLUMN_NAMES = {"Key", "Value"};

    DynamicMapPropertyTabContainer(Module module, PropertyAccessor propertyAccessor) {
        MapTableColumnModel columnModel = new MapTableColumnModel(module);
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
            add(new ClickableLabel(message("properties.type.map.table.add"), AddItem, table::addEmptyRow));
            add(new ClickableLabel(message("properties.type.map.table.remove"), RemoveItem, table::removeSelectedRow));
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
        public Object getValueAt(int row, int column) {
            return super.getValueAt(row, column);
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return true;
        }

        @SuppressWarnings("unchecked")
        private void updateAccessorData() {
            Map<String, String> updated = new LinkedHashMap<>();
            getDataVector().forEach(vector -> {
                String key = VectorUtils.getOrEmptyIfNull((Vector<String>) vector, 0); // 0 is the key
                String value = ScriptUtils.asScript(VectorUtils.getOrEmptyIfNull((Vector<String>) vector, 1)); // 1 is the value
                updated.put(key, value);
            });
            accessor.set(updated);
        }

        private void initialize() {
            Map<String, String> map = this.accessor.get();
            if (map != null) {
                map.forEach((key, value) -> addRow(new Object[]{key, ScriptUtils.unwrap(value)}));
            }
        }
    }

    class MapTableColumnModel extends DefaultTableColumnModel {

        private DynamicMapCellEditor cellEditor;
        private DynamicMapCellRenderer cellRenderer;

        MapTableColumnModel(Module  module) {
            this.cellEditor = new DynamicMapCellEditor(module);
            this.cellRenderer = new DynamicMapCellRenderer(module);

            // Column 1 (the map key)
            TableColumn keyColumn = new TableColumn(0);
            keyColumn.setHeaderValue(COLUMN_NAMES[0]);
            addColumn(keyColumn);

            // Column 2 (the map value)
            TableColumn valueColumn = new TableColumn(1);
            valueColumn.setHeaderValue(COLUMN_NAMES[1]);
            valueColumn.setCellRenderer(cellRenderer);
            valueColumn.setCellEditor(cellEditor);
            addColumn(valueColumn);
        }
    }

    private class DynamicMapCellEditor implements TableCellEditor, Disposable {

        private final ScriptEditor editor;
        private CellEditorListener listener;
        private DisposablePanel content;

        DynamicMapCellEditor(Module module) {
            JLabel codeIcon = new JLabel(Icons.Script.Code);
            this.editor = new DynamicValueScriptEditor(module);
            this.content = ContainerFactory.createLabelNextToComponent(codeIcon, editor, false);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.editor.setValue(value == null ? StringUtils.EMPTY : (String) value);
            return this.content;
        }

        @Override
        public Object getCellEditorValue() {
            return editor.getValue();
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        @Override
        public boolean stopCellEditing() {
            if (listener != null) {
                listener.editingStopped(new ChangeEvent(this));
            }
            return true;
        }

        @Override
        public void cancelCellEditing() {
            if (listener != null) {
                listener.editingStopped(new ChangeEvent(this));
            }
        }

        @Override
        public void addCellEditorListener(CellEditorListener listener) {
            this.listener = listener;
        }

        @Override
        public void removeCellEditorListener(CellEditorListener listener) {
            this.listener = listener;
        }

        @Override
        public void dispose() {
            DisposableUtils.dispose(editor);
        }
    }


    private class DynamicMapCellRenderer implements TableCellRenderer, Disposable {

        private DisposablePanel content;
        private final DynamicValueScriptEditor editor;

        DynamicMapCellRenderer(Module module) {
            JLabel codeIcon = new JLabel(Icons.Script.Code);
            this.editor = new DynamicValueScriptEditor(module);
            this.content = ContainerFactory.createLabelNextToComponent(codeIcon, editor, false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            this.editor.setValue(value == null ? StringUtils.EMPTY : (String) value);
            return this.content;
        }

        @Override
        public void dispose() {
            DisposableUtils.dispose(this.editor);
        }
    }
}

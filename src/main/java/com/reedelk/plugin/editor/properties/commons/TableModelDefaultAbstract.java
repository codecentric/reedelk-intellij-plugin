package com.reedelk.plugin.editor.properties.commons;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;

public abstract class TableModelDefaultAbstract extends DefaultTableModel implements DisposableTableModel {

    private static final int ROWS = 0;
    private static final int COLUMNS = 2; // a Map table has two columns

    public TableModelDefaultAbstract() {
        super(ROWS, COLUMNS);
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        super.setValueAt(aValue, row, column);
        onUpdate(getDataVector());
    }

    @Override
    public void addRow(Object[] rowData) {
        super.addRow(rowData);
        onUpdate(getDataVector());
    }

    @Override
    public void removeRow(int row) {
        super.removeRow(row);
        onUpdate(getDataVector());
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    protected abstract void onUpdate(Vector<?> data);
}

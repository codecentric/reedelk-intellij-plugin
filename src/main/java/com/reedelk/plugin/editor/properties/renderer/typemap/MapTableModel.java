package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.reedelk.plugin.editor.properties.commons.DisposableTableModel;

import javax.swing.table.DefaultTableModel;
import java.util.Vector;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class MapTableModel extends DefaultTableModel implements DisposableTableModel {

    private final transient Consumer<Vector<Vector<?>>> dataUpdater;

    MapTableModel(Consumer<Vector<Vector<?>>> dataUpdater) {
        super(0, 2); // a Map table has two columns
        this.dataUpdater = dataUpdater;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        super.setValueAt(aValue, row, column);
        dataUpdater.accept(getDataVector());
    }

    @Override
    public void addRow(Object[] rowData) {
        super.addRow(rowData);
        dataUpdater.accept(getDataVector());
    }

    @Override
    public void removeRow(int row) {
        super.removeRow(row);
        dataUpdater.accept(getDataVector());
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }
}

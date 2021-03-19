package de.codecentric.reedelk.plugin.editor.properties.commons;

import javax.swing.table.TableModel;

public interface DisposableTableModel extends TableModel {

    default void addRow(Object[] rowData) {
        throw new UnsupportedOperationException();
    }

    default void removeRow(int row) {
        throw new UnsupportedOperationException();
    }

    default Object[] createRow() {
        return new Object[]{};
    }
}

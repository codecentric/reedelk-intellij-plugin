package com.reedelk.plugin.editor.properties.widget;

import com.intellij.openapi.Disposable;
import com.intellij.ui.table.JBTable;

import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class DisposableTable extends JBTable implements Disposable {


    public DisposableTable(TableModel tableModel, TableColumnModel tableColumnModel) {
        super(tableModel, tableColumnModel);
    }

    @Override
    public void dispose() {
        if (getModel() != null) {
            TableModel tableModel = getModel();
            if (tableModel instanceof Disposable) {
                ((Disposable) tableModel).dispose();
            }
        }
        if (getColumnModel() != null) {
            TableColumnModel columnModel = getColumnModel();
            if (columnModel instanceof Disposable) {
                ((Disposable) columnModel).dispose();
            }
        }
    }
}

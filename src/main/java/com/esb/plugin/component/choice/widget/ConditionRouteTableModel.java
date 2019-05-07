package com.esb.plugin.component.choice.widget;

import com.esb.plugin.component.choice.ChoiceConditionRoutePair;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ConditionRouteTableModel extends AbstractTableModel {

    private final GraphSnapshot snapshot;
    private final List<ChoiceConditionRoutePair> conditionRouteList;

    public ConditionRouteTableModel(List<ChoiceConditionRoutePair> conditionRouteList, GraphSnapshot snapshot) {
        this.conditionRouteList = conditionRouteList;
        this.snapshot = snapshot;
    }

    @Override
    public int getRowCount() {
        return conditionRouteList.size();
    }

    @Override
    public int getColumnCount() {
        return ConditionRouteColumns.COLUMN_NAMES.length;
    }

    @Override
    public String getColumnName(int col) {
        return ConditionRouteColumns.COLUMN_NAMES[col];
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        // row 0 and column 0 is not editable (this is the default route)
        return row != conditionRouteList.size() - 1 && col != 1;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object returnValue = null;
        ChoiceConditionRoutePair conditionRoute = conditionRouteList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                returnValue = conditionRoute.getCondition();
                break;
            case 1:
                returnValue = conditionRoute.getNext();
                break;
        }
        return returnValue;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        ChoiceConditionRoutePair conditionRoute = conditionRouteList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                conditionRoute.setCondition((String) value);
                break;
            case 1:
                conditionRoute.setNext((GraphNode) value);
                break;
        }
        snapshot.onDataChange();
    }

}

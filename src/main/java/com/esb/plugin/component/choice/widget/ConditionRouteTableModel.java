package com.esb.plugin.component.choice.widget;

import com.esb.plugin.component.choice.ChoiceConditionRoutePair;
import com.esb.plugin.graph.node.GraphNode;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ConditionRouteTableModel extends AbstractTableModel {

    private List<ChoiceConditionRoutePair> conditionRouteList = new ArrayList<>();

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
        return row != 0 || col != 0;
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
    }

    void addConditionRoutePair(ChoiceConditionRoutePair pair) {
        conditionRouteList.add(pair);
        int rowChanged = conditionRouteList.size();
        fireTableRowsInserted(rowChanged, rowChanged);
    }

}

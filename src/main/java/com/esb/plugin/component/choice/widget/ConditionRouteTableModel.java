package com.esb.plugin.component.choice.widget;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ConditionRouteTableModel extends AbstractTableModel {

    private List<ConditionRoutePair> conditionRouteList = new ArrayList<>();

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
        ConditionRoutePair conditionRoute = conditionRouteList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                returnValue = conditionRoute.condition;
                break;
            case 1:
                returnValue = conditionRoute.route;
                break;
        }
        return returnValue;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        ConditionRoutePair conditionRoute = conditionRouteList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                conditionRoute.condition = (String) value;
                break;
            case 1:
                conditionRoute.route = (String) value;
                break;
        }
    }

    void addConditionRoutePair(String condition, String route) {
        this.conditionRouteList.add(new ConditionRoutePair(condition, route));
    }

}

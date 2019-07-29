package com.esb.plugin.component.type.router.widget;

import com.esb.plugin.component.type.router.RouterConditionRoutePair;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.graph.node.GraphNode;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ConditionRouteTableModel extends AbstractTableModel {

    private final FlowSnapshot snapshot;
    private final List<RouterConditionRoutePair> conditionRouteList;

    public ConditionRouteTableModel(List<RouterConditionRoutePair> conditionRouteList, FlowSnapshot snapshot) {
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
        return !(row == conditionRouteList.size() - 1 && col == 1);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object returnValue = "";
        RouterConditionRoutePair conditionRoute = conditionRouteList.get(rowIndex);
        switch (columnIndex) {
            case 1:
                returnValue = conditionRoute.getCondition();
                break;
            case 2:
                returnValue = conditionRoute.getNext();
                break;
        }
        return returnValue;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        RouterConditionRoutePair conditionRoute = conditionRouteList.get(rowIndex);
        switch (columnIndex) {
            case 1:
                conditionRoute.setCondition((String) value);
                break;
            case 2:
                conditionRoute.setNext((GraphNode) value);
                break;
        }
        snapshot.onDataChange();
    }

}

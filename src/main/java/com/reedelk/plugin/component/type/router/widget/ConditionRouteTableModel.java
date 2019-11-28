package com.reedelk.plugin.component.type.router.widget;

import com.reedelk.plugin.component.type.router.RouterConditionRoutePair;
import com.reedelk.plugin.editor.properties.commons.PropertyTable;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.runtime.api.commons.ScriptUtils;
import com.reedelk.runtime.api.commons.StringUtils;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class ConditionRouteTableModel extends AbstractTableModel implements PropertyTable.PropertyTableModel {

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
        return !(row == conditionRouteList.size() - 1 && col == 0) && !(col == 1);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Object returnValue = StringUtils.EMPTY;
        RouterConditionRoutePair conditionRoute = conditionRouteList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                returnValue = ScriptUtils.unwrap(conditionRoute.getCondition());
                break;
            case 1:
                returnValue = conditionRoute.getNext();
                break;
        }
        return returnValue;
    }

    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
        RouterConditionRoutePair conditionRoute = conditionRouteList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                conditionRoute.setCondition(ScriptUtils.asScript((String) value));
                break;
            case 1:
                conditionRoute.setNext((GraphNode) value);
                break;
        }
        snapshot.onDataChange();
    }
}

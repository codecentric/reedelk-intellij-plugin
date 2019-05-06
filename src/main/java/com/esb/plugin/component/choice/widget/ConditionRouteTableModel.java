package com.esb.plugin.component.choice.widget;

import com.esb.plugin.component.ComponentData;
import com.esb.plugin.component.choice.ChoiceConditionRoutePair;
import com.esb.plugin.graph.GraphSnapshot;
import com.esb.plugin.graph.node.GraphNode;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class ConditionRouteTableModel extends AbstractTableModel {

    private final GraphNode node;
    private final GraphSnapshot snapshot;
    private final List<ChoiceConditionRoutePair> conditionRouteList;

    public ConditionRouteTableModel(List<ChoiceConditionRoutePair> conditionRouteList, GraphNode node, GraphSnapshot snapshot) {
        this.conditionRouteList = conditionRouteList;
        this.snapshot = snapshot;
        this.node = node;
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
        snapshot.onDataChange();
    }

    void addConditionRoutePair(ChoiceConditionRoutePair pair) {
        conditionRouteList.add(pair);

        int rowChanged = conditionRouteList.size();
        fireTableRowsInserted(rowChanged, rowChanged);


        ComponentData component = node.component();

        List<ChoiceConditionRoutePair> data = conditionRouteList;
        Optional<ChoiceConditionRoutePair> otherwise = data
                .stream()
                .filter(choiceConditionRoutePair ->
                        choiceConditionRoutePair.getCondition().equals("otherwise"))
                .findAny();
        otherwise.ifPresent(choiceConditionRoutePair ->
                component.set("otherwise", choiceConditionRoutePair.getNext()));

        List<ChoiceConditionRoutePair> whenConditions = data
                .stream()
                .filter(choiceConditionRoutePair ->
                        !choiceConditionRoutePair.getCondition().equals("otherwise"))
                .collect(toList());

        component.set("when", whenConditions);

        snapshot.onDataChange();
    }


}

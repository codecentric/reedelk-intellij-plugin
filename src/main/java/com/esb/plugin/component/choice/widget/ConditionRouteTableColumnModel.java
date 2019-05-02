package com.esb.plugin.component.choice.widget;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

class ConditionRouteTableColumnModel extends DefaultTableColumnModel {

    ConditionRouteTableColumnModel(JComboBox routesComboBox) {
        // Column 0
        TableColumn conditionColumn = new TableColumn(0);
        conditionColumn.setHeaderValue(ConditionRouteColumns.COLUMN_NAMES[0]);
        addColumn(conditionColumn);

        // Column 1
        TableColumn routeColumn = new TableColumn(1);
        routeColumn.setHeaderValue(ConditionRouteColumns.COLUMN_NAMES[1]);
        routeColumn.setCellEditor(new DefaultCellEditor(routesComboBox));
        addColumn(routeColumn);
    }

}

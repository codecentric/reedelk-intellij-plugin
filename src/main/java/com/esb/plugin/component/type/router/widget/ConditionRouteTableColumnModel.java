package com.esb.plugin.component.type.router.widget;

import com.esb.plugin.graph.node.GraphNode;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import static com.esb.internal.commons.JsonParser.Implementor;

class ConditionRouteTableColumnModel extends DefaultTableColumnModel {

    ConditionRouteTableColumnModel() {
        // Column 0
        TableColumn conditionColumn = new TableColumn(0);
        conditionColumn.setHeaderValue(ConditionRouteColumns.COLUMN_NAMES[0]);
        addColumn(conditionColumn);

        // Column 1
        TableColumn routeColumn = new TableColumn(1);
        routeColumn.setHeaderValue(ConditionRouteColumns.COLUMN_NAMES[1]);
        routeColumn.setCellRenderer(new RoutesCellRenderer());

        addColumn(routeColumn);
    }

    class RoutesCellRenderer extends DefaultTableCellRenderer {
        @Override
        protected void setValue(Object value) {
            GraphNode node = (GraphNode) value;
            String description = node.componentData().get(Implementor.description());
            setText(description);
        }
    }

}
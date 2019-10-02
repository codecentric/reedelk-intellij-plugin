package com.reedelk.plugin.component.type.router.widget;

import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.graph.node.GraphNode;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.*;

import static com.reedelk.runtime.commons.JsonParser.Implementor;

class ConditionRouteTableColumnModel extends DefaultTableColumnModel {

    private static final int EDIT_COLUMN_WIDTH = 22;

    ConditionRouteTableColumnModel() {
        // Column 0 (Edit)
        TableColumn editColumn = new TableColumn(0);
        editColumn.setHeaderValue(ConditionRouteColumns.COLUMN_NAMES[0]);
        editColumn.setCellRenderer(new EditCellRenderer());
        editColumn.setMaxWidth(EDIT_COLUMN_WIDTH);
        addColumn(editColumn);

        // Column 1 (Condition)
        TableColumn conditionColumn = new TableColumn(1);
        conditionColumn.setHeaderValue(ConditionRouteColumns.COLUMN_NAMES[1]);
        addColumn(conditionColumn);

        // Column 1 (Route)
        TableColumn routeColumn = new TableColumn(2);
        routeColumn.setHeaderValue(ConditionRouteColumns.COLUMN_NAMES[2]);
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

    class EditCellRenderer implements TableCellRenderer {

        private JLabel content = new JLabel();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (table.getModel().getRowCount() - 1 != row) {
                content.setIcon(Icons.Script.Edit);
            } else {
                content.setIcon(null);
            }
            content.setBorder(JBUI.Borders.empty(2));
            return content;
        }
    }
}

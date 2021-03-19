package de.codecentric.reedelk.plugin.component.type.router.widget;

import com.intellij.openapi.module.Module;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.UIUtil;
import de.codecentric.reedelk.plugin.editor.properties.commons.DisposableTableColumnModelFactory;
import de.codecentric.reedelk.plugin.editor.properties.commons.StripedRowCellRenderer;
import de.codecentric.reedelk.plugin.editor.properties.commons.TableDynamicCellEditor;
import de.codecentric.reedelk.plugin.editor.properties.commons.TableDynamicCellRenderer;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import de.codecentric.reedelk.runtime.api.commons.StringUtils;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;

import static de.codecentric.reedelk.runtime.commons.JsonParser.Implementor;

class ConditionRouteTableColumnModelFactory implements DisposableTableColumnModelFactory {

    private final TableDynamicCellRenderer cellRenderer;
    private final TableDynamicCellEditor conditionCellEditor;

    ConditionRouteTableColumnModelFactory(Module module,
                                          String componentPropertyPath,
                                          ContainerContext context) {
        cellRenderer = new TableDynamicCellRenderer(module, componentPropertyPath, context);
        conditionCellEditor = new TableDynamicCellEditor(module, componentPropertyPath, context);
    }

    @Override
    public void create(JBTable table) {
        // Column 0 (Condition)
        TableColumn conditionColumn = table.getColumnModel().getColumn(0);
        conditionColumn.setHeaderValue(ConditionRouteColumns.COLUMN_NAMES[0]);
        conditionColumn.setCellRenderer(cellRenderer);
        conditionColumn.setCellEditor(conditionCellEditor);

        // Column 1 (Route)
        TableColumn routeColumn = table.getColumnModel().getColumn(1);
        routeColumn.setHeaderValue(ConditionRouteColumns.COLUMN_NAMES[1]);
        routeColumn.setCellRenderer(new RoutesCellRenderer());
    }

    static class RoutesCellRenderer extends StripedRowCellRenderer {
        @Override
        protected void setValue(Object value) {
            GraphNode node = (GraphNode) value;
            StringBuilder routeDisplayName = new StringBuilder(node.componentData().getDisplayName());
            String description = node.componentData().get(Implementor.description());
            if (StringUtils.isNotBlank(description)) {
                routeDisplayName.append(" (").append(description).append(")");
            }
            setText(routeDisplayName.toString());
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel component = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (isSelected) {
                // The Route field is not selectable, therefore we don't change
                // its color when it is selected.
                component.setBackground(row % 2 == 0 ? UIUtil.getDecoratedRowColor() : UIUtil.getTableBackground());
                component.setForeground(UIUtil.getTextFieldForeground());
            }
            return component;
        }
    }
}

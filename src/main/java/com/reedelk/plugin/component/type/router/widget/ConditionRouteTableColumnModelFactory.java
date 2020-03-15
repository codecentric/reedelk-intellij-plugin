package com.reedelk.plugin.component.type.router.widget;

import com.intellij.openapi.module.Module;
import com.intellij.ui.table.JBTable;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposableTableColumnModelFactory;
import com.reedelk.plugin.editor.properties.commons.TableDynamicCellEditor;
import com.reedelk.plugin.editor.properties.commons.TableDynamicCellRenderer;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.runtime.api.commons.StringUtils;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import static com.reedelk.runtime.commons.JsonParser.Implementor;

class ConditionRouteTableColumnModelFactory implements DisposableTableColumnModelFactory {

    private final Module module;
    private final ContainerContext context;

    ConditionRouteTableColumnModelFactory(Module module, ContainerContext context) {
        this.module = module;
        this.context = context;
    }

    @Override
    public void create(JBTable table) {
        TableDynamicCellRenderer cellRenderer = new TableDynamicCellRenderer(module, context);
        TableDynamicCellEditor conditionCellEditor = new TableDynamicCellEditor(module, context);

        // Column 0 (Condition)
        TableColumn conditionColumn = table.getColumnModel().getColumn(0);
        conditionColumn.setHeaderValue(ConditionRouteColumns.COLUMN_NAMES[1]);
        conditionColumn.setCellRenderer(cellRenderer);
        conditionColumn.setCellEditor(conditionCellEditor);

        // Column 1 (Route)
        TableColumn routeColumn = table.getColumnModel().getColumn(1);
        routeColumn.setHeaderValue(ConditionRouteColumns.COLUMN_NAMES[1]);
        routeColumn.setCellRenderer(new RoutesCellRenderer());
    }

    static class RoutesCellRenderer extends DefaultTableCellRenderer {

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
    }
}

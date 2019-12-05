package com.reedelk.plugin.component.type.router.widget;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.commons.TableColumnModelUtils;
import com.reedelk.plugin.editor.properties.CommitPropertiesListener;
import com.reedelk.plugin.editor.properties.commons.TableDynamicCellEditor;
import com.reedelk.plugin.editor.properties.commons.TableDynamicCellRenderer;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.runtime.api.commons.StringUtils;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import static com.reedelk.runtime.commons.JsonParser.Implementor;

class ConditionRouteTableColumnModel extends DefaultTableColumnModel implements Disposable, CommitPropertiesListener {

    private transient final MessageBusConnection busConnection;
    private transient final TableDynamicCellRenderer cellRenderer;
    private transient final TableDynamicCellEditor conditionCellEditor;

    ConditionRouteTableColumnModel(Module module) {
        cellRenderer = new TableDynamicCellRenderer(module);
        conditionCellEditor = new TableDynamicCellEditor(module);

        // Column 0 (Condition)
        TableColumn conditionColumn = new TableColumn(0);
        conditionColumn.setHeaderValue(ConditionRouteColumns.COLUMN_NAMES[1]);
        conditionColumn.setCellRenderer(cellRenderer);
        conditionColumn.setCellEditor(conditionCellEditor);
        addColumn(conditionColumn);

        // Column 1 (Route)
        TableColumn routeColumn = new TableColumn(1);
        routeColumn.setHeaderValue(ConditionRouteColumns.COLUMN_NAMES[1]);
        routeColumn.setCellRenderer(new RoutesCellRenderer());
        addColumn(routeColumn);

        busConnection = module.getProject().getMessageBus().connect();
        busConnection.subscribe(CommitPropertiesListener.COMMIT_TOPIC, this);
    }

    @Override
    public void dispose() {
        DisposableUtils.dispose(cellRenderer);
        DisposableUtils.dispose(conditionCellEditor);
        busConnection.disconnect();
    }

    @Override
    public void onCommit() {
        TableColumnModelUtils.onCommit(this);
    }

    class RoutesCellRenderer extends DefaultTableCellRenderer {
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

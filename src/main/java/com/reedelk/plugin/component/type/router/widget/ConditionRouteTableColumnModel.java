package com.reedelk.plugin.component.type.router.widget;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.editor.properties.CommitPropertiesListener;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.renderer.commons.ScriptEditor;
import com.reedelk.plugin.editor.properties.renderer.typedynamicvalue.DynamicValueScriptEditor;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.runtime.api.commons.StringUtils;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.*;
import java.awt.*;
import java.util.EventObject;

import static com.reedelk.plugin.commons.Icons.Script;
import static com.reedelk.runtime.commons.JsonParser.Implementor;

class ConditionRouteTableColumnModel extends DefaultTableColumnModel implements Disposable, CommitPropertiesListener {

    private transient final MessageBusConnection busConnection;
    private transient final ConditionCellRenderer cellRenderer;
    private transient final ConditionCellEditor conditionCellEditor;

    ConditionRouteTableColumnModel(Module module) {
        cellRenderer = new ConditionCellRenderer(module);
        conditionCellEditor = new ConditionCellEditor(module);

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
        conditionCellEditor.stopCellEditing();
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

    private class ConditionCellEditor implements TableCellEditor, Disposable {

        private final ScriptEditor editor;
        private CellEditorListener listener;
        private DisposablePanel content;

        ConditionCellEditor(Module module) {
            JLabel codeIcon = new JLabel(Script.Code);
            this.editor = new DynamicValueScriptEditor(module);
            this.content = ContainerFactory.createLabelNextToComponent(codeIcon, editor, false);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.editor.setValue((String) value);
            return this.content;
        }

        @Override
        public Object getCellEditorValue() {
            return editor.getValue();
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        @Override
        public boolean stopCellEditing() {
            if (listener != null) {
                listener.editingStopped(new ChangeEvent(this));
            }
            return true;
        }

        @Override
        public void cancelCellEditing() {
            if (listener != null) {
                listener.editingStopped(new ChangeEvent(this));
            }
        }

        @Override
        public void addCellEditorListener(CellEditorListener listener) {
            this.listener = listener;
        }

        @Override
        public void removeCellEditorListener(CellEditorListener listener) {
            this.listener = listener;
        }

        @Override
        public void dispose() {
            DisposableUtils.dispose(editor);
        }
    }


    private class ConditionCellRenderer implements TableCellRenderer, Disposable {

        private DisposablePanel content;
        private final DynamicValueScriptEditor editor;

        ConditionCellRenderer(Module module) {
            JLabel codeIcon = new JLabel(Script.Code);
            this.editor = new DynamicValueScriptEditor(module);
            this.content = ContainerFactory.createLabelNextToComponent(codeIcon, editor, false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            this.editor.setValue((String) value);
            return this.content;
        }

        @Override
        public void dispose() {
            DisposableUtils.dispose(this.editor);
        }
    }
}

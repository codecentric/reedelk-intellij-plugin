package com.reedelk.plugin.component.type.router.widget;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.editor.properties.CommitPropertiesListener;
import com.reedelk.plugin.editor.properties.widget.ClickableLabel;
import com.reedelk.plugin.editor.properties.widget.ContainerFactory;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import com.reedelk.plugin.editor.properties.widget.input.script.ScriptContextManager;
import com.reedelk.plugin.editor.properties.widget.input.script.editor.DynamicValueScriptEditor;
import com.reedelk.plugin.graph.node.GraphNode;
import com.reedelk.runtime.api.commons.StringUtils;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.*;
import java.awt.*;
import java.util.EventObject;

import static com.reedelk.runtime.commons.JsonParser.Implementor;

class ConditionRouteTableColumnModel extends DefaultTableColumnModel implements Disposable, CommitPropertiesListener {

    private MessageBusConnection busConnection;
    private final ConditionCellRenderer cellRenderer;
    private final ConditionCellEditor conditionCellEditor;

    ConditionRouteTableColumnModel(JComponent parent, Module module, ScriptContextManager scriptContextManager) {
        cellRenderer = new ConditionCellRenderer(module, scriptContextManager);
        conditionCellEditor = new ConditionCellEditor(parent, module, scriptContextManager);

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

        private final DynamicValueScriptEditor editor;
        private CellEditorListener listener;
        private DisposablePanel content;

        ConditionCellEditor(JComponent parent, Module module, ScriptContextManager scriptContextManager) {
            this.editor = new DynamicValueScriptEditor(module.getProject(), scriptContextManager);
            this.editor.addOnEditDone(parent::requestFocusInWindow);
            JLabel codeIcon = new ClickableLabel(Icons.Script.Code, Icons.Script.Code, () -> {
            });
            content = ContainerFactory.createLabelNextToComponent(codeIcon, editor.getComponent(), false);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            editor.setValue((String) value);
            return content;
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

        private final DynamicValueScriptEditor editor;
        private DisposablePanel content;

        ConditionCellRenderer(Module module, ScriptContextManager scriptContextManager) {
            this.editor = new DynamicValueScriptEditor(module.getProject(), scriptContextManager);
            JLabel codeIcon = new ClickableLabel(Icons.Script.Code, Icons.Script.Code, () -> {
            });
            content = ContainerFactory.createLabelNextToComponent(codeIcon, editor.getComponent(), false);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            this.editor.setValue((String) value);
            return content;
        }

        @Override
        public void dispose() {
            DisposableUtils.dispose(editor);
        }
    }
}

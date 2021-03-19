package de.codecentric.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.UIUtil;
import de.codecentric.reedelk.plugin.commons.DisposableUtils;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typedynamicvalue.DynamicValueScriptEditor;
import de.codecentric.reedelk.runtime.api.commons.StringUtils;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.util.EventObject;

import static de.codecentric.reedelk.plugin.commons.Icons.Script.Code;

public class TableDynamicCellEditor implements TableCellEditor, Disposable {

    private final ScriptEditor editor;
    private final DisposablePanel content;
    private CellEditorListener listener;

    public TableDynamicCellEditor(Module module, String componentPropertyPath, ContainerContext context) {
        JBLabel codeIcon = new JBLabel(Code);
        codeIcon.setOpaque(true);

        this.editor = new DynamicValueScriptEditor(module, componentPropertyPath, context);
        this.content = ContainerFactory.createLabelNextToComponentWithoutOuterBorder(codeIcon, editor);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.editor.setBackground(row % 2 != 0 ? UIUtil.getDecoratedRowColor() : UIUtil.getTableBackground());
        this.editor.setValue(value == null ? StringUtils.EMPTY : (String) value);
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

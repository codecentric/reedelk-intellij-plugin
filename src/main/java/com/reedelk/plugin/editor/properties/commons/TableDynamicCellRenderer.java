package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.editor.properties.renderer.typedynamicvalue.DynamicValueScriptEditor;
import com.reedelk.runtime.api.commons.StringUtils;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class TableDynamicCellRenderer implements TableCellRenderer, Disposable {

    private final DisposablePanel content;
    private final DynamicValueScriptEditor editor;

    public TableDynamicCellRenderer(Module module) {
        JLabel codeIcon = new JLabel(Icons.Script.Code);
        this.editor = new DynamicValueScriptEditor(module);
        this.content = ContainerFactory.createLabelNextToComponent(codeIcon, editor, false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        this.editor.setValue(value == null ? StringUtils.EMPTY : (String) value);
        return this.content;
    }

    @Override
    public void dispose() {
        DisposableUtils.dispose(this.editor);
    }
}
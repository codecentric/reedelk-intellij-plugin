package com.reedelk.plugin.editor.properties.renderer.typemap.custom;

import com.intellij.ui.components.JBLabel;
import com.reedelk.plugin.commons.Icons;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static com.intellij.icons.AllIcons.Actions.EditSource;


public class MapTableCustomEditButtonCell extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener, MouseListener {

    private JTable table;
    private JPanel edit;
    private JPanel render;
    private MapTableCustomEditButtonAction action;

    private Object editorValue;
    private boolean isButtonColumnEditor;

    public MapTableCustomEditButtonCell(JTable table, MapTableCustomEditButtonAction action) {
        this.table = table;
        this.action = action;


        JBLabel editLabel = new JBLabel(Icons.Misc.Info);
        editLabel.setIcon(EditSource);
        editLabel.setVerticalAlignment(SwingConstants.CENTER);

        edit = new JPanel();
        edit.setLayout(new FlowLayout());
        edit.add(editLabel);

        JBLabel renderLabel = new JBLabel(Icons.Misc.Info);
        renderLabel.setIcon(EditSource);
        renderLabel.setVerticalAlignment(SwingConstants.CENTER);

        render = new JPanel();
        render.setLayout(new FlowLayout());
        render.add(renderLabel);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        return render;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.editorValue = value;
        return edit;
    }

    @Override
    public Object getCellEditorValue() {
        return editorValue;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        fireEditingStopped();
    }

    @Override
    public void mousePressed(MouseEvent event) {
        if (table.isEditing() && table.getCellEditor() == this) {
            isButtonColumnEditor = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        if (isButtonColumnEditor && table.isEditing()) {
            action.onClick(editorValue);
            table.getCellEditor().stopCellEditing();
            fireEditingStopped();
        }
        isButtonColumnEditor = false;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
    }

    @Override
    public void mouseEntered(MouseEvent event) {
    }

    @Override
    public void mouseExited(MouseEvent event) {
    }
}

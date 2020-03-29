package com.reedelk.plugin.editor.properties.commons;

import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.UIUtil;
import com.reedelk.plugin.commons.Icons;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;

import static com.intellij.icons.AllIcons.Actions.EditSource;


public class TableEditButtonCellEditor extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener, MouseListener, MouseMotionListener {

    private static final Cursor CURSOR_HAND = new Cursor(Cursor.HAND_CURSOR);
    private static final Cursor CURSOR_DEFAULT = new Cursor(Cursor.DEFAULT_CURSOR);

    private JPanel edit;
    private JTable table;
    private JPanel render;
    private boolean isButtonColumnEditor;

    private transient Object editorValue;
    private transient TableCustomEditButtonAction action;

    public interface TableCustomEditButtonAction {
        void onClick(Object editorValue);
    }

    public TableEditButtonCellEditor(JTable table, TableCustomEditButtonAction action) {
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

        table.addMouseListener(this);
        table.addMouseMotionListener(this);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        render.setBackground(row % 2 == 0 ? UIUtil.getDecoratedRowColor() : UIUtil.getTableBackground());
        render.setForeground(UIUtil.getTextFieldForeground());
        return render;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.editorValue = value;
        edit.setBackground(row % 2 == 0 ? UIUtil.getDecoratedRowColor() : UIUtil.getTableBackground());
        edit.setForeground(UIUtil.getTextFieldForeground());
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
    public void mouseDragged(MouseEvent e) {
        // We are not interested in detecting mouse dragged event.
    }

    @Override
    public void mouseMoved(MouseEvent event) {
        if (table.columnAtPoint(event.getPoint()) == 1) {
            table.setCursor(CURSOR_HAND);
        } else {
            table.setCursor(CURSOR_DEFAULT);
        }
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        // Not used
    }

    @Override
    public void mouseEntered(MouseEvent event) {
        // Not used
    }

    @Override
    public void mouseExited(MouseEvent event) {
        // Not used
    }
}

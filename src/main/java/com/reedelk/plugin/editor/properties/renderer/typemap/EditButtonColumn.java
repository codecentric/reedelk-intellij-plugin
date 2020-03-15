package com.reedelk.plugin.editor.properties.renderer.typemap;

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

/**
 * The ButtonColumn class provides a renderer and an editor that looks like a
 * JButton. The renderer and editor will then be used for a specified column
 * in the table. The TableModel will contain the String to be displayed on
 * the button.
 * <p>
 * The button can be invoked by a mouse click or by pressing the space bar
 * when the cell has focus. Optionally a mnemonic can be set to invoke the
 * button. When the button is invoked the provided Action is invoked. The
 * source of the Action will be the table. The action command will contain
 * the model row number of the button that was clicked.
 */
public class EditButtonColumn extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener, MouseListener {
    private JTable table;

    private MapTableCustomColumnModel.ActionHandler action;
    private JPanel edit;
    private JPanel render;

    private boolean isButtonColumnEditor;
    private Object editorValue;

    public EditButtonColumn(JTable table, MapTableCustomColumnModel.ActionHandler action) {
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
            table.getCellEditor().stopCellEditing();
            action.onClick(editorValue);
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

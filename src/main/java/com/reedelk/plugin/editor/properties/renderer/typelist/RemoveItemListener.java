package com.reedelk.plugin.editor.properties.renderer.typelist;

import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RemoveItemListener implements ActionListener, ListDataListener {

    private final JBList<Object> list;
    private final JButton removeButton;
    private final DefaultListModel<Object> listModel;

    RemoveItemListener(@NotNull JBList<Object> list,
                       @NotNull DefaultListModel<Object> listModel,
                       @NotNull JButton removeButton) {
        this.list = list;
        this.listModel = listModel;
        this.removeButton = removeButton;
        this.listModel.addListDataListener(this);
        checkButtonEnabled();
    }

    public void actionPerformed(ActionEvent e) {
        //This method can be called only if
        //there's a valid selection
        //so go ahead and remove whatever's selected.
        int index = list.getSelectedIndex();
        listModel.remove(index);

        int size = listModel.getSize();

        if (size > 0) {
            if (index == listModel.getSize()) {
                //removed item in last position
                index--;
            }
            list.setSelectedIndex(index);
            list.ensureIndexIsVisible(index);
        }
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        checkButtonEnabled();
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        checkButtonEnabled();
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        checkButtonEnabled();
    }

    private void checkButtonEnabled() {
        if (listModel.getSize() > 0) {
            removeButton.setEnabled(true);
        } else {
            removeButton.setEnabled(false);
        }
    }
}

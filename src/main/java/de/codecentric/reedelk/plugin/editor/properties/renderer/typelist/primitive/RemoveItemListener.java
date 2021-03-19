package de.codecentric.reedelk.plugin.editor.properties.renderer.typelist.primitive;

import de.codecentric.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

public class RemoveItemListener implements ClickableLabel.OnClickAction, ListDataListener {

    private final JBList<Object> list;
    private final ClickableLabel removeButton;
    private final DefaultListModel<Object> listModel;

    RemoveItemListener(@NotNull JBList<Object> list,
                       @NotNull DefaultListModel<Object> listModel,
                       @NotNull ClickableLabel removeButton) {
        this.list = list;
        this.listModel = listModel;
        this.removeButton = removeButton;
        this.listModel.addListDataListener(this);
        checkButtonEnabled();
    }

    @Override
    public void onClick() {
        // This method can be called only if there's a valid selection
        // so go ahead and remove whatever's selected.
        int index = list.getSelectedIndex();
        if (index >= 0) {
            listModel.remove(index);
        }

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

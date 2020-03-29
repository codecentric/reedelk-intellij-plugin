package com.reedelk.plugin.editor.properties.renderer.typelist.primitive;

import com.intellij.ui.components.JBList;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class AddItemListener implements ClickableLabel.OnClickAction, DocumentListener {

    private ClickableLabel addButton;
    private final JBList<Object> list;
    private final JTextField itemTextField;
    private final DefaultListModel<Object> listModel;

    private boolean alreadyEnabled = false;

    public AddItemListener(@NotNull JBList<Object> list,
                           @NotNull DefaultListModel<Object> listModel,
                           @NotNull ClickableLabel addButton,
                           @NotNull JTextField itemTextField) {
        this.list = list;
        this.addButton = addButton;
        this.listModel = listModel;
        this.itemTextField = itemTextField;
        this.itemTextField.getDocument().addDocumentListener(this);
    }

    @Override
    public void onClick() {

        String itemValue = itemTextField.getText();

        // User didn't type in a unique name...
        if (itemValue.equals(StringUtils.EMPTY) || alreadyInList(itemValue)) {
            Toolkit.getDefaultToolkit().beep();
            itemTextField.requestFocusInWindow();
            itemTextField.selectAll();
            return;
        }

        int index = list.getSelectedIndex(); //get selected index
        if (index == -1) { //no selection, so insert at beginning
            index = 0;
        } else {           //add after the selected item
            index++;
        }

        listModel.insertElementAt(itemTextField.getText(), index);

        //Reset the text field.
        itemTextField.requestFocusInWindow();
        itemTextField.setText("");

        //Select the new item and make it visible.
        list.setSelectedIndex(index);
        list.ensureIndexIsVisible(index);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        enableButton();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        handleEmptyTextField(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        if (!handleEmptyTextField(e)) {
            enableButton();
        }
    }

    //This method tests for string equality. You could certainly
    //get more sophisticated about the algorithm.  For example,
    //you might want to ignore white space and capitalization.
    private boolean alreadyInList(String name) {
        return listModel.contains(name);
    }

    private void enableButton() {
        if (!alreadyEnabled) {
            addButton.setEnabled(true);
        }
    }

    private boolean handleEmptyTextField(DocumentEvent event) {
        if (event.getDocument().getLength() <= 0) {
            addButton.setEnabled(false);
            alreadyEnabled = false;
            return true;
        }
        return false;
    }
}

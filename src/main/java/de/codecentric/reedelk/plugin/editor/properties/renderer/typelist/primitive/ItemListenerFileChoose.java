package de.codecentric.reedelk.plugin.editor.properties.renderer.typelist.primitive;

import de.codecentric.reedelk.plugin.editor.properties.commons.ClickableLabel;
import de.codecentric.reedelk.plugin.editor.properties.commons.FileChooseInputFieldWithEraseBtn;
import com.intellij.ui.components.JBList;
import de.codecentric.reedelk.runtime.api.commons.StringUtils;

import javax.swing.*;
import java.awt.*;

public class ItemListenerFileChoose implements ClickableLabel.OnClickAction {

    private final FileChooseInputFieldWithEraseBtn.PropertyAccessorInMemory propertyAccessorInMemory;
    private final FileChooseInputFieldWithEraseBtn fileChoose;
    private final JBList<Object> list;
    private final DefaultListModel<Object> listModel;

    public ItemListenerFileChoose(JBList<Object> theList,
                                  DefaultListModel<Object> theModel,
                                  ClickableLabel theLabel,
                                  FileChooseInputFieldWithEraseBtn fileChoose,
                                  FileChooseInputFieldWithEraseBtn.PropertyAccessorInMemory propertyAccessorInMemory) {
        this.propertyAccessorInMemory = propertyAccessorInMemory;
        this.list = theList;
        this.listModel = theModel;
        this.fileChoose = fileChoose;
    }

    @Override
    public void onClick() {
        String itemValue = this.propertyAccessorInMemory.get();

        // User didn't type in a unique name...
        if (itemValue.equals(StringUtils.EMPTY) || alreadyInList(itemValue)) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }

        int index = list.getSelectedIndex(); //get selected index
        if (index == -1) { //no selection, so insert at beginning
            index = 0;
        } else {           //add after the selected item
            index++;
        }

        listModel.insertElementAt(itemValue, index);

        //Reset the text field.
        this.fileChoose.erase();


        //Select the new item and make it visible.
        list.setSelectedIndex(index);
        list.ensureIndexIsVisible(index);
    }

    //This method tests for string equality. You could certainly
    //get more sophisticated about the algorithm.  For example,
    //you might want to ignore white space and capitalization.
    private boolean alreadyInList(String name) {
        return listModel.contains(name);
    }

}

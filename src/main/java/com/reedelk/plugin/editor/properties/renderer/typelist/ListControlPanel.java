package com.reedelk.plugin.editor.properties.renderer.typelist;

import com.intellij.ui.components.JBList;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.renderer.commons.StringInputField;

import javax.swing.*;
import java.awt.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static javax.swing.SwingConstants.VERTICAL;

public class ListControlPanel extends DisposablePanel {

    private static final Dimension DIMENSION_ITEM_TO_ADD_BTN = new Dimension(200, 10);

    public ListControlPanel(JBList<Object> list, DefaultListModel<Object> model) {

        JButton delete = new JButton(message("properties.type.list.delete"));
        delete.addActionListener(new RemoveItemListener(list, model, delete));

        JTextField itemValueTextField = new StringInputField(message("properties.type.list.item.hint"));
        itemValueTextField.setPreferredSize(DIMENSION_ITEM_TO_ADD_BTN);

        JButton add = new JButton(message("properties.type.list.add"));
        add.addActionListener(new AddItemListener(list, model, add, itemValueTextField));

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        add(delete);
        add(new JSeparator(VERTICAL));
        add(add);
        add(itemValueTextField);
    }
}

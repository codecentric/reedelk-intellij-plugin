package com.reedelk.plugin.editor.properties.renderer.typelist;

import com.intellij.ui.components.JBList;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.renderer.commons.StringInputField;

import javax.swing.*;

import static com.intellij.icons.AllIcons.General.Add;
import static com.intellij.icons.AllIcons.General.Remove;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static javax.swing.SwingConstants.VERTICAL;

public class ListControlPanel extends DisposablePanel {

    public ListControlPanel(JBList<Object> list, DefaultListModel<Object> model) {

        ClickableLabel delete = new ClickableLabel(message("properties.type.list.delete"), Remove, Remove);
        delete.setBorder(JBUI.Borders.empty(2, 4));
        delete.setAction(new RemoveItemListener(list, model, delete));

        JTextField itemValueTextField = new StringInputField(message("properties.type.list.item.hint"));

        ClickableLabel add = new ClickableLabel(message("properties.type.list.add"), Add, Add);
        add.setBorder(JBUI.Borders.empty(4));
        add.setAction(new AddItemListener(list, model, add, itemValueTextField));

        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        add(delete);
        add(new JSeparator(VERTICAL));
        add(add);
        add(itemValueTextField);
    }
}

package com.reedelk.plugin.editor.properties.renderer.typelist;

import com.intellij.ui.components.JBList;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.StringInputField;

import javax.swing.*;
import java.awt.*;

import static com.intellij.icons.AllIcons.General.Add;
import static com.intellij.icons.AllIcons.General.Remove;
import static com.intellij.util.ui.JBUI.Borders;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static javax.swing.SwingConstants.VERTICAL;

public class ListControlPanel extends DisposablePanel {

    public ListControlPanel(JBList<Object> list, DefaultListModel<Object> model) {
        JTextField itemValueTextField = new StringInputField(message("properties.type.list.item.hint"));
        ControlsContainer controlsContainer = new ControlsContainer(list, model, itemValueTextField);

        setLayout(new BorderLayout());
        setBorder(Borders.empty(1, 0));
        add(controlsContainer, BorderLayout.WEST);
        add(itemValueTextField, BorderLayout.CENTER);
    }

    static class ControlsContainer extends DisposablePanel {

        ControlsContainer(JBList<Object> list, DefaultListModel<Object> model, JTextField itemValueTextField) {
            ClickableLabel delete = new ClickableLabel(message("properties.type.list.delete"), Remove, Remove);
            delete.setBorder(Borders.emptyRight(4));
            delete.setAction(new RemoveItemListener(list, model, delete));

            ClickableLabel add = new ClickableLabel(message("properties.type.list.add"), Add, Add);
            add.setBorder(Borders.empty(0, 4));
            add.setAction(new AddItemListener(list, model, add, itemValueTextField));

            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            add(delete);
            add(new JSeparator(VERTICAL));
            add(add);
        }
    }
}

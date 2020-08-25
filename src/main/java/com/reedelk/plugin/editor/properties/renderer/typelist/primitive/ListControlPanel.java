package com.reedelk.plugin.editor.properties.renderer.typelist.primitive;

import com.intellij.ui.components.JBList;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.StringInputField;

import javax.swing.*;
import java.awt.*;

import static com.intellij.icons.AllIcons.General.Add;
import static com.intellij.icons.AllIcons.General.Remove;
import static com.intellij.util.ui.JBUI.Borders;
import static com.reedelk.plugin.editor.properties.commons.ClickableLabel.OnClickAction;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static javax.swing.SwingConstants.VERTICAL;

public class ListControlPanel extends DisposablePanel {

    public ListControlPanel(JBList<Object> list,
                            DefaultListModel<Object> model,
                            String inputHint) {


        StringInputField inputField = new StringInputField(inputHint);
        AddActionProvider addActionProvider = (theList, theModel, addLabel) ->
                new ItemListenerTextField(theList, theModel, addLabel, inputField);

        ControlsContainer controls = new ControlsContainer(list, model, addActionProvider);
        setLayout(new BorderLayout());
        setBorder(Borders.empty(1, 0));
        add(controls, BorderLayout.WEST);
        add(inputField, BorderLayout.CENTER);
    }

    static class ControlsContainer extends DisposablePanel {

        ControlsContainer(JBList<Object> list, DefaultListModel<Object> model, AddActionProvider provider) {
            ClickableLabel delete = new ClickableLabel(message("properties.type.list.delete"), Remove, Remove);
            delete.setBorder(Borders.emptyRight(4));
            delete.setAction(new RemoveItemListener(list, model, delete));

            ClickableLabel add = new ClickableLabel(message("properties.type.list.add"), Add, Add);
            add.setBorder(Borders.empty(0, 4));

            OnClickAction addAction = provider.provide(list, model, add);
            add.setAction(addAction);

            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            add(delete);
            add(new JSeparator(VERTICAL));
            add(add);
        }
    }

    interface AddActionProvider {
        OnClickAction provide(JBList<Object> list, DefaultListModel<Object> model, ClickableLabel addLabel);
    }
}

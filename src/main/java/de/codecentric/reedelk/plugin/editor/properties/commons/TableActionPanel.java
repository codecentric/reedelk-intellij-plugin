package de.codecentric.reedelk.plugin.editor.properties.commons;

import javax.swing.*;
import java.awt.*;

import static com.intellij.icons.AllIcons.General.Add;
import static com.intellij.icons.AllIcons.General.Remove;
import static com.intellij.util.ui.JBUI.Borders;
import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;
import static javax.swing.SwingConstants.VERTICAL;

public class TableActionPanel extends DisposablePanel {

    public TableActionPanel(ClickableLabel.OnClickAction addAction, ClickableLabel.OnClickAction removeAction) {
        ControlsContainer controlsContainer = new ControlsContainer(addAction, removeAction);

        setLayout(new BorderLayout());
        setBorder(Borders.empty(3, 0));
        add(controlsContainer, BorderLayout.WEST);
    }

    static class ControlsContainer extends DisposablePanel {
        ControlsContainer(ClickableLabel.OnClickAction addAction, ClickableLabel.OnClickAction removeAction) {

            ClickableLabel add = new ClickableLabel(message("properties.type.map.table.add"), Add, addAction);
            add.setBorder(Borders.empty(3,0,3,4));

            ClickableLabel remove = new ClickableLabel(message("properties.type.map.table.remove"), Remove, removeAction);
            remove.setBorder(Borders.empty(3, 4));

            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            add(add);
            add(new JSeparator(VERTICAL));
            add(remove);
        }
    }
}

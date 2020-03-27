package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.DisposableTable;

import javax.swing.*;
import java.awt.*;

import static com.intellij.icons.AllIcons.General.Add;
import static com.intellij.icons.AllIcons.General.Remove;
import static com.intellij.util.ui.JBUI.Borders;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static javax.swing.SwingConstants.VERTICAL;

public class MapTableActionPanel extends DisposablePanel {

    public MapTableActionPanel(DisposableTable table) {
        ControlsContainer controlsContainer = new ControlsContainer(table);

        setLayout(new BorderLayout());
        setBorder(Borders.empty(4, 0));
        add(controlsContainer, BorderLayout.WEST);
    }

    static class ControlsContainer extends DisposablePanel {
        ControlsContainer(DisposableTable table) {

            ClickableLabel add = new ClickableLabel(message("properties.type.map.table.add"), Add, table::addEmptyRow);
            add.setBorder(Borders.empty(4,0,4,4));

            ClickableLabel remove = new ClickableLabel(message("properties.type.map.table.remove"), Remove, table::removeSelectedRow);
            remove.setBorder(Borders.empty(4,4,4,4));

            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            add(add);
            add(new JSeparator(VERTICAL));
            add(remove);
        }
    }
}

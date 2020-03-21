package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.DisposableTable;

import java.awt.*;

import static com.intellij.icons.AllIcons.General.Add;
import static com.intellij.icons.AllIcons.General.Remove;
import static com.reedelk.plugin.message.ReedelkBundle.message;

public class MapTableActionPanel extends DisposablePanel {

    public MapTableActionPanel(DisposableTable table) {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(new ClickableLabel(message("properties.type.map.table.add"), Add, table::addEmptyRow));
        add(new ClickableLabel(message("properties.type.map.table.remove"), Remove, table::removeSelectedRow));
    }
}

package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.openapi.project.Project;
import com.reedelk.plugin.editor.properties.commons.*;

import javax.swing.*;
import java.awt.*;

import static com.intellij.icons.AllIcons.General.Add;
import static com.intellij.icons.AllIcons.General.Remove;
import static com.reedelk.plugin.message.ReedelkBundle.message;

class MapPropertyTabContainer extends DisposablePanel {

    MapPropertyTabContainer(Project project,
                            DisposableTableModel tableModel,
                            DisposableTableColumnModelFactory columnModelFactory) {
        DisposableTable table = new DisposableTable(project, tableModel, columnModelFactory);
        JPanel actionPanel = new TableActionPanel(table);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(actionPanel);
        add(table);
    }

    static class TableActionPanel extends DisposablePanel {
        TableActionPanel(DisposableTable table) {
            setLayout(new FlowLayout(FlowLayout.LEFT));
            add(new ClickableLabel(message("properties.type.map.table.add"), Add, table::addEmptyRow));
            add(new ClickableLabel(message("properties.type.map.table.remove"), Remove, table::removeSelectedRow));
        }
    }
}

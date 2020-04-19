package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.Sizes;
import com.reedelk.plugin.editor.properties.commons.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders;
import static com.reedelk.plugin.editor.properties.commons.ClickableLabel.OnClickAction;

public class MapTableContainer extends DisposablePanel {

    public MapTableContainer(@NotNull Module module,
                             @NotNull DisposableTableModel tableModel,
                             @NotNull DisposableTableColumnModelFactory columnModelFactory) {
        setLayout(new BorderLayout());
        setBorder(Borders.empty());
        add(new TableContainer(module, tableModel, columnModelFactory), BorderLayout.CENTER);
    }

    static class TableContainer extends DisposablePanel {

        TableContainer(@NotNull Module module,
                       @NotNull DisposableTableModel tableModel,
                       @NotNull DisposableTableColumnModelFactory columnModelFactory) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            DisposableScrollableTable disposablePropertyTable =
                    new DisposableScrollableTable(module.getProject(), Sizes.Table.DEFAULT, tableModel, columnModelFactory);

            OnClickAction addAction = disposablePropertyTable::addEmptyRow;
            OnClickAction removeAction = disposablePropertyTable::removeSelectedRow;

            JPanel actionPanel = new TableActionPanel(addAction, removeAction);
            add(actionPanel);
            add(disposablePropertyTable);
        }
    }
}

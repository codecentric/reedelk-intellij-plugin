package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.Sizes;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.DisposableTable;
import com.reedelk.plugin.editor.properties.commons.DisposableTableColumnModelFactory;
import com.reedelk.plugin.editor.properties.commons.DisposableTableModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders;

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
            DisposableTable disposablePropertyTable =
                    new DisposableTable(module.getProject(), Sizes.Table.DEFAULT, tableModel, columnModelFactory);
            JPanel actionPanel = new MapTableActionPanel(disposablePropertyTable);
            add(actionPanel);
            add(disposablePropertyTable);
        }
    }
}

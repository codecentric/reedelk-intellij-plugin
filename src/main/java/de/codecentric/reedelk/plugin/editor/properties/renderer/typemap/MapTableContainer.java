package de.codecentric.reedelk.plugin.editor.properties.renderer.typemap;

import de.codecentric.reedelk.plugin.editor.properties.commons.*;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.plugin.commons.Sizes;
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
            DisposableScrollableTable disposablePropertyTable =
                    new DisposableScrollableTable(module.getProject(), Sizes.Table.DEFAULT, tableModel, columnModelFactory);

            ClickableLabel.OnClickAction addAction = disposablePropertyTable::addEmptyRow;
            ClickableLabel.OnClickAction removeAction = disposablePropertyTable::removeSelectedRow;

            JPanel actionPanel = new TableActionPanel(addAction, removeAction);
            add(actionPanel);
            add(disposablePropertyTable);
        }
    }
}

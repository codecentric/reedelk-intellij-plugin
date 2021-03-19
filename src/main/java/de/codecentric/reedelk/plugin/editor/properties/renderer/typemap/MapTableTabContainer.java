package de.codecentric.reedelk.plugin.editor.properties.renderer.typemap;

import de.codecentric.reedelk.plugin.editor.properties.commons.*;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.plugin.commons.Sizes;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;

public class MapTableTabContainer extends DisposablePanel {

    private DisposableScrollableTable disposablePropertyTable;

    public MapTableTabContainer(@NotNull Module module,
                                @NotNull DisposableTableModel tableModel,
                                @NotNull DisposableTableColumnModelFactory columnModelFactory) {

        addComponentListener(new ComponentListenerAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {

                if (disposablePropertyTable != null) return;

                DisposablePanel mapContainer = new DisposablePanel();
                mapContainer.setLayout(new BoxLayout(mapContainer, BoxLayout.Y_AXIS));
                disposablePropertyTable =
                        new DisposableScrollableTable(module.getProject(), Sizes.Table.TABBED, tableModel, columnModelFactory);

                ClickableLabel.OnClickAction addAction = disposablePropertyTable::addEmptyRow;
                ClickableLabel.OnClickAction removeAction = disposablePropertyTable::removeSelectedRow;

                JPanel actionPanel = new TableActionPanel(addAction, removeAction);
                mapContainer.add(actionPanel);
                mapContainer.add(disposablePropertyTable);

                removeAll();
                add(mapContainer, BorderLayout.CENTER);

            }
        });

        setLayout(new BorderLayout());
        add(new PanelWithText.LoadingContentPanel());
    }
}

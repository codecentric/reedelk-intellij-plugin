package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.Sizes;
import com.reedelk.plugin.editor.properties.commons.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;

import static com.reedelk.plugin.editor.properties.commons.PanelWithText.LoadingContentPanel;

public class MapTableTabContainer extends DisposablePanel {

    public MapTableTabContainer(@NotNull Module module,
                                @NotNull DisposableTableModel tableModel,
                                @NotNull DisposableTableColumnModelFactory columnModelFactory) {

        addComponentListener(new ComponentListenerAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {

                DisposablePanel mapContainer = new DisposablePanel();
                mapContainer.setLayout(new BoxLayout(mapContainer, BoxLayout.Y_AXIS));
                DisposableTable disposablePropertyTable =
                        new DisposableTable(module.getProject(), Sizes.Table.TABBED, tableModel, columnModelFactory);

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
        add(new LoadingContentPanel());
    }
}

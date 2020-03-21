package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.properties.commons.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;

public class MapTableTabContainer extends DisposablePanel {

    private boolean loaded = false;

    public MapTableTabContainer(@NotNull Module module,
                         @NotNull DisposableTableModel tableModel,
                         @NotNull DisposableTableColumnModelFactory columnModelFactory) {
        setLayout(new BorderLayout());
        add(new LoadingContentPanel());

        addComponentListener(new ComponentListenerAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                // If the content was already loaded, there is no need to loading it again.
                if (loaded) return;

                // Lazy loading of Table content. We do it so that we can optimize the rendering
                // of the component's properties.
                SwingUtilities.invokeLater(() -> {
                    DisposablePanel mapContainer = new DisposablePanel();
                    mapContainer.setLayout(new BoxLayout(mapContainer, BoxLayout.Y_AXIS));
                    DisposableTable disposablePropertyTable = new DisposableTable(module.getProject(), tableModel, columnModelFactory);
                    JPanel actionPanel = new MapTableActionPanel(disposablePropertyTable);
                    mapContainer.add(actionPanel);
                    mapContainer.add(disposablePropertyTable);

                    loaded = true;

                    removeAll();
                    add(mapContainer, BorderLayout.CENTER);
                });
            }
        });
    }
}

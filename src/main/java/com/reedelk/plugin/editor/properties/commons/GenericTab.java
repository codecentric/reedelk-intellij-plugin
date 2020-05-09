package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.application.ApplicationManager;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.ComponentEvent;
import java.util.function.Supplier;

import static com.intellij.util.ui.JBUI.Borders.empty;
import static com.reedelk.plugin.editor.properties.commons.PanelWithText.LoadingContentPanel;

public class GenericTab extends DisposableScrollPane {

    private static final Border CONTENT_BORDER = empty(5, 15, 15, 0);

    private boolean loaded = false;

    public GenericTab(Supplier<JComponent> componentSupplier) {
        super(VERTICAL_SCROLLBAR_AS_NEEDED, HORIZONTAL_SCROLLBAR_NEVER);

        addComponentListener(new ComponentListenerAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                // If the content was already loaded, there is no need to loading it again.
                if (loaded) return;

                // Lazy loading of Table content. We do it so that we can optimize the rendering
                // of the component's properties.
                ApplicationManager.getApplication().invokeLater(() -> {

                    JComponent jComponent = componentSupplier.get();

                    if (jComponent instanceof JTable) {
                        // Router (the JBTable adds the header to the scroll pane automatically).
                        jComponent.setOpaque(false);
                        setViewportView(jComponent);

                    } else {
                        DisposablePanel toAdd = ContainerFactory.pushTop(jComponent);
                        toAdd.setBorder(CONTENT_BORDER);
                        setViewportView(toAdd);
                    }

                    loaded = true;
                });
            }
        });

        setViewportView(ContainerFactory.pushTop(new LoadingContentPanel()));
        getViewport().setOpaque(true);
        setOpaque(true);
        setViewportBorder(empty());
        setBorder(empty());
    }
}

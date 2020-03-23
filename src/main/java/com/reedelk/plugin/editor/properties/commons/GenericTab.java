package com.reedelk.plugin.editor.properties.commons;

import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.util.function.Supplier;

import static com.reedelk.plugin.editor.properties.commons.PanelWithText.LoadingContentPanel;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;
import static javax.swing.Box.createGlue;

public class GenericTab extends DisposableScrollPane {

    private boolean loaded = false;

    public GenericTab(Supplier<JComponent> componentSupplier) {
        DisposablePanel panel = new DisposablePanel();
        panel.setLayout(new BorderLayout());
        panel.add(new LoadingContentPanel(), NORTH);
        panel.add(createGlue(), CENTER);

        addComponentListener(new ComponentListenerAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                // If the content was already loaded, there is no need to loading it again.
                if (loaded) return;

                // Lazy loading of Table content. We do it so that we can optimize the rendering
                // of the component's properties.
                SwingUtilities.invokeLater(() -> {

                    JComponent jComponent = componentSupplier.get();

                    panel.removeAll();
                    panel.add(jComponent, NORTH);
                    panel.add(createGlue(), CENTER);

                    loaded = true;
                });
            }
        });

        createVerticalScrollBar();
        setBorder(JBUI.Borders.empty());
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        setViewportView(panel);
    }
}

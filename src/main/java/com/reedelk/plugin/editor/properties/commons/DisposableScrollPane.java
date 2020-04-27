package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.Disposable;
import com.intellij.ui.components.JBScrollPane;

import java.awt.*;

public class DisposableScrollPane extends JBScrollPane implements Disposable {

    public DisposableScrollPane(int verticalScrollbarAsNeeded, int horizontalScrollbarNever) {
        super(verticalScrollbarAsNeeded, horizontalScrollbarNever);
    }

    public DisposableScrollPane() {
    }

    @Override
    public void dispose() {
        // Dispose all the children components implementing
        // the Disposable interface. This is for instance needed
        // to correctly release resources of script editor.
        Component[] components = getViewport().getComponents();
        for (Component component : components) {
            if (component instanceof Disposable) {
                ((Disposable) component).dispose();
            }
        }
    }
}

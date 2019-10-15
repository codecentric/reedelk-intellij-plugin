package com.reedelk.plugin.editor.properties.widget;

import com.intellij.openapi.Disposable;
import com.intellij.ui.components.JBScrollPane;

import java.awt.*;

public class DisposableScrollPane extends JBScrollPane implements Disposable {

    @Override
    public void dispose() {
        // Dispose all the children components implementing
        // the Disposable interface. This is for instance needed
        // to correctly release resources of Javascript Editor.
        Component[] components = getViewport().getComponents();
        for (Component component : components) {
            if (component instanceof Disposable) {
                ((Disposable) component).dispose();
            }
        }
    }
}
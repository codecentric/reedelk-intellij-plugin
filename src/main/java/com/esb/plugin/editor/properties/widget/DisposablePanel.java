package com.esb.plugin.editor.properties.widget;

import com.esb.plugin.editor.properties.PropertiesBasePanel;
import com.intellij.openapi.Disposable;

import java.awt.*;

public class DisposablePanel extends PropertiesBasePanel implements Disposable {
    @Override
    public void dispose() {
        // Dispose all the children components implementing
        // the disposable interface. This is for instance needed
        // to correctly release resources of Javascript Editor.
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof Disposable) {
                ((Disposable) component).dispose();
            }
        }
    }
}

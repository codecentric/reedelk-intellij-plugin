package de.codecentric.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.Disposable;
import com.intellij.ui.components.JBTabbedPane;

import java.awt.*;

public class DisposableTabbedPane extends JBTabbedPane implements Disposable {

    public DisposableTabbedPane(int tabPlacement) {
        super(tabPlacement);
    }

    @Override
    public void dispose() {
        // Dispose all the children components implementing
        // the disposable interface. This is for instance needed
        // to correctly release resources of script editor.
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof Disposable) {
                ((Disposable) component).dispose();
            }
        }
    }
}

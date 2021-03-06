package de.codecentric.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.ui.ThreeComponentsSplitter;
import com.intellij.openapi.util.Disposer;

import java.awt.*;

public class DisposableThreeComponentsSplitter extends ThreeComponentsSplitter {

    public DisposableThreeComponentsSplitter(boolean vertical, Disposable disposable) {
        // Add: super(vertical, disposable) when IntelliJ version is supported.
        super(vertical);
    }

    @Override
    public void dispose() {
        // Dispose all the children components implementing
        // the Disposable interface. This is for instance needed
        // to correctly release resources of script editor.
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof Disposable) {
                ((Disposable) component).dispose();
            }
        }
        Disposer.dispose(this);
        super.dispose();
    }
}

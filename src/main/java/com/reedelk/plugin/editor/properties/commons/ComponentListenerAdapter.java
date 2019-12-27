package com.reedelk.plugin.editor.properties.commons;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * An adapter class which avoids clients to override all methods from
 * the Listener when only one method is actually needed.
 */
public class ComponentListenerAdapter implements ComponentListener {

    @Override
    public void componentResized(ComponentEvent e) {
        // If needed can be overridden by inheritors.
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        // If needed can be overridden by inheritors.
    }

    @Override
    public void componentShown(ComponentEvent e) {
        // If needed can be overridden by inheritors.
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        // If needed can be overridden by inheritors.
    }
}

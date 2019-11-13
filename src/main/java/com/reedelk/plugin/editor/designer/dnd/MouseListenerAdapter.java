package com.reedelk.plugin.editor.designer.dnd;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public interface MouseListenerAdapter extends MouseListener {

    @Override
    default void mouseClicked(MouseEvent e) {
        // nothing to do
    }

    @Override
    default void mouseExited(MouseEvent e) {
        // nothing to do
    }

    @Override
    default void mouseEntered(MouseEvent e) {
        // nothing to do
    }
}

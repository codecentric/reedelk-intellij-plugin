package com.esb.plugin.editor.properties.widget;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ActionableCommandButton extends JLabel implements MouseListener {

    private ActionableCommandListener listener;

    public ActionableCommandButton(String text, Icon icon) {
        setText(text);
        setIcon(icon);
        addMouseListener(this);
    }

    public void addListener(ActionableCommandListener listener) {
        this.listener = listener;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (listener != null) {
            listener.onClick();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    public interface ActionableCommandListener {
        void onClick();
    }
}

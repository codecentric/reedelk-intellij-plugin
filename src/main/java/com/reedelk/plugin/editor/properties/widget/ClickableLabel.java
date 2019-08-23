package com.reedelk.plugin.editor.properties.widget;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ClickableLabel extends JLabel implements MouseListener {

    private final OnClickAction action;

    public ClickableLabel(String text, Icon icon, Icon disabledIcon) {
        this(text, icon, disabledIcon, null);
    }

    public ClickableLabel(String text, Icon icon, Icon disabledIcon, OnClickAction action) {
        setText(text);
        setIcon(icon);
        setDisabledIcon(disabledIcon);
        addMouseListener(this);
        this.action = action;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.action.onClick();
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

    public interface OnClickAction {
        void onClick();
    }
}

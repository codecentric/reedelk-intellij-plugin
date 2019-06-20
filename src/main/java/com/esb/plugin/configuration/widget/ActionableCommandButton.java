package com.esb.plugin.configuration.widget;

import com.esb.plugin.editor.properties.widget.input.ConfigSelector;
import com.esb.plugin.service.module.impl.ConfigMetadata;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class ActionableCommandButton extends JLabel implements MouseListener, ConfigSelector.SelectListener {

    private ConfigMetadata selectedMetadata;
    private ActionableCommandListener listener;

    ActionableCommandButton(String text, Icon icon) {
        setText(text);
        setIcon(icon);
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (listener != null) {
            listener.onClick(selectedMetadata);
        }
    }

    @Override
    public void onSelect(ConfigMetadata configMetadata) {
        this.selectedMetadata = configMetadata;
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
        void onClick(ConfigMetadata selectedConfig);
    }

    public void addListener(ActionableCommandListener listener) {
        this.listener = listener;
    }
}

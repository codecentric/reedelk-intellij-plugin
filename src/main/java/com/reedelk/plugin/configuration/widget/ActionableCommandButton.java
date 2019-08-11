package com.reedelk.plugin.configuration.widget;

import com.reedelk.plugin.editor.properties.widget.input.ConfigSelector;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class ActionableCommandButton extends JLabel implements MouseListener, ConfigSelector.SelectListener {

    private ConfigMetadata selectedMetadata;

    ActionableCommandButton(String text, Icon icon, Icon disabledIcon) {
        setText(text);
        setIcon(icon);
        setDisabledIcon(disabledIcon);
        addMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        onClick(selectedMetadata);
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

    protected abstract void onClick(ConfigMetadata selectedMetadata);

}

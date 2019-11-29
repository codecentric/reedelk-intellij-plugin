package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;

import javax.swing.*;
import java.awt.event.MouseEvent;

public abstract class ActionableCommandButton extends ClickableLabel {

    private ConfigMetadata selectedMetadata;

    ActionableCommandButton(String text, Icon icon, Icon disabledIcon) {
        super(text, icon, disabledIcon);
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        onClick(selectedMetadata);
    }

    public void onSelect(ConfigMetadata configMetadata) {
        this.selectedMetadata = configMetadata;
    }

    protected abstract void onClick(ConfigMetadata selectedMetadata);

}

package com.reedelk.plugin.editor.properties;

import com.reedelk.plugin.commons.PopupUtils;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;

import javax.swing.*;
import java.awt.event.MouseEvent;

public class ClickableLabelWithTooltip extends ClickableLabel {

    private final String tooltipText;

    public ClickableLabelWithTooltip(String tooltipText, Icon icon) {
        super(icon);
        this.tooltipText = tooltipText;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        PopupUtils.info(tooltipText, this);
    }
}

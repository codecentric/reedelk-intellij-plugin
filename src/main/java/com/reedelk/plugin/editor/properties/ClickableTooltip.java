package com.reedelk.plugin.editor.properties;

import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.commons.PopupUtils;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;

import java.awt.event.MouseEvent;

public class ClickableTooltip extends ClickableLabel {

    private final String tooltipText;

    public ClickableTooltip(String tooltipText) {
        super(Icons.Misc.Info);
        this.tooltipText = tooltipText;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        PopupUtils.info(tooltipText, this);
    }
}
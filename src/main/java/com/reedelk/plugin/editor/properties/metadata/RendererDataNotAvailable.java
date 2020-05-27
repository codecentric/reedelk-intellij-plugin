package com.reedelk.plugin.editor.properties.metadata;

import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.service.module.impl.component.metadata.MetadataActualInputDTO;

import javax.swing.*;
import java.awt.*;

public class RendererDataNotAvailable implements Renderer {

    @Override
    public boolean accept(MetadataActualInputDTO actualInputDTO) {
        return actualInputDTO == null;
    }

    @Override
    public void render(JComponent parent, MetadataActualInputDTO actualInputDTO) {
        DataNotAvailable dataNotAvailable = new DataNotAvailable();
        dataNotAvailable.setBorder(JBUI.Borders.empty(2, 5));
        FormBuilder.get().addFullWidthAndHeight(dataNotAvailable, parent);
    }

    // TODO: Add the text in the reedelk bundle.
    protected static class DataNotAvailable extends DisposablePanel {
        DataNotAvailable() {
            super(new BorderLayout());
            add(new JBLabel(RendererUtils.htmlText("Data is not available, make sure that the previous " +
                    "component define @ComponentOutput annotation."), JLabel.CENTER), BorderLayout.CENTER);
            setBorder(JBUI.Borders.empty(5));
        }
    }
}

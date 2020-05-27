package com.reedelk.plugin.editor.properties.metadata;

import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.service.module.impl.component.metadata.MetadataActualInputDTO;

import javax.swing.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class RendererActualInputNotAvailable implements Renderer {

    @Override
    public boolean accept(MetadataActualInputDTO actualInputDTO) {
        return actualInputDTO == null;
    }

    @Override
    public void render(JComponent parent, MetadataActualInputDTO actualInputDTO) {
        String text = message("metadata.actual.input.not.available");
        InputNotAvailablePanel dataNotAvailable = new InputNotAvailablePanel(text);
        FormBuilder.get().addFullWidthAndHeight(dataNotAvailable, parent);
    }
}

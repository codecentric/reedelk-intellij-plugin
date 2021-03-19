package de.codecentric.reedelk.plugin.editor.properties.metadata;

import de.codecentric.reedelk.plugin.editor.properties.commons.FormBuilder;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.MetadataActualInputDTO;

import javax.swing.*;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;

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

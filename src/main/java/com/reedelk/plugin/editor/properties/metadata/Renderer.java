package com.reedelk.plugin.editor.properties.metadata;

import com.reedelk.plugin.service.module.impl.component.metadata.MetadataActualInputDTO;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

public interface Renderer {

    List<Renderer> RENDERERS = Collections.unmodifiableList(asList(
            new RendererMultipleMessages(),
            new RendererActualInputNotAvailable(),
            new RendererNotFound(),
            new RendererDefault()));

    static void on(JComponent parent, MetadataActualInputDTO actualInputDTO) {
        for (Renderer renderer : RENDERERS) {
            if (renderer.accept(actualInputDTO)) {
                renderer.render(parent, actualInputDTO);
                return;
            }
        }
    }

    boolean accept(MetadataActualInputDTO actualInputDTO);

    void render(JComponent parent, MetadataActualInputDTO actualInputDTO);
}

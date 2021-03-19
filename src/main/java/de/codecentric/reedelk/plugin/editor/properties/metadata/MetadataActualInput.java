package de.codecentric.reedelk.plugin.editor.properties.metadata;

import de.codecentric.reedelk.plugin.editor.properties.commons.DisposablePanel;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.MetadataDTO;

public class MetadataActualInput extends AbstractMetadataInputPanel {

    @Override
    void render(MetadataDTO metadataDTO, DisposablePanel parent) {
        Renderer.on(parent, metadataDTO.getActualInput());
    }
}

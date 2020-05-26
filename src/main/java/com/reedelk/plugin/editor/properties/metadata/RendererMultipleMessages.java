package com.reedelk.plugin.editor.properties.metadata;

import com.intellij.ui.components.JBLabel;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.service.module.impl.component.metadata.MetadataActualInputDTO;

import javax.swing.*;

public class RendererMultipleMessages implements Renderer {

    @Override
    public boolean accept(MetadataActualInputDTO actualInputDTO) {
        return actualInputDTO.isMultipleMessages();
    }

    @Override
    public void render(JComponent parent, MetadataActualInputDTO actualInputDTO) {
        String title = RendererUtils.htmlTitle("Multiple messages", "List<Message>");
        JBLabel label = new JBLabel(title);
        FormBuilder.get().addFullWidthAndHeight(label, parent);
    }
}

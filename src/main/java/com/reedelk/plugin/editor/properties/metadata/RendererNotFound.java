package com.reedelk.plugin.editor.properties.metadata;

import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.service.module.impl.component.metadata.MetadataActualInputDTO;

import javax.swing.*;

import static com.reedelk.plugin.editor.properties.metadata.RendererUtils.htmlText;
import static com.reedelk.plugin.message.ReedelkBundle.message;

public class RendererNotFound implements Renderer {

    @Override
    public boolean accept(MetadataActualInputDTO actualInputDTO) {
        return actualInputDTO.isNotFound();
    }

    @Override
    public void render(JComponent parent, MetadataActualInputDTO actualInputDTO) {
        JBLabel label = new JBLabel(htmlText(message("metadata.input.not.found")));
        label.setBorder(JBUI.Borders.empty(2, 5));
        FormBuilder.get().addFullWidthAndHeight(label, parent);
    }
}

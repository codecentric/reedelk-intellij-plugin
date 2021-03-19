package de.codecentric.reedelk.plugin.editor.properties.metadata;

import de.codecentric.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.MetadataActualInputDTO;

import javax.swing.*;

import static de.codecentric.reedelk.plugin.editor.properties.metadata.RendererUtils.htmlText;
import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;

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

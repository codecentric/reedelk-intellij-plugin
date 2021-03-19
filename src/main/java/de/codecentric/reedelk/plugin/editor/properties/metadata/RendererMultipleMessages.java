package de.codecentric.reedelk.plugin.editor.properties.metadata;

import de.codecentric.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.MetadataActualInputDTO;

import javax.swing.*;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;

public class RendererMultipleMessages implements Renderer {

    @Override
    public boolean accept(MetadataActualInputDTO actualInputDTO) {
        return actualInputDTO.isMultipleMessages();
    }

    @Override
    public void render(JComponent parent, MetadataActualInputDTO actualInputDTO) {
        String title = RendererUtils.htmlTitle(
                message("metadata.multiple.messages.title"),
                message("metadata.multiple.messages.trailing"));
        JBLabel label = new JBLabel(title);
        label.setBorder(JBUI.Borders.empty(2, 5));
        FormBuilder.get().addFullWidthAndHeight(label, parent);
    }
}

package com.reedelk.plugin.editor.properties.metadata;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.service.module.impl.component.metadata.MetadataDTO;
import com.reedelk.plugin.service.module.impl.component.metadata.MetadataExpectedInputDTO;
import com.reedelk.runtime.api.commons.StringUtils;

import javax.swing.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class MetadataExpectedInput extends AbstractMetadataInputPanel {

    public MetadataExpectedInput() {
        setBorder(JBUI.Borders.empty(0, 1, 0, 8));
        setBackground(JBColor.WHITE);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

    @Override
    void render(MetadataDTO metadataDTO, DisposablePanel parent) {
        MetadataExpectedInputDTO expectedInput = metadataDTO.getExpectedInput();
        if (expectedInput != null) {
            render(expectedInput, parent);
        } else {
            String text = message("metadata.expected.input.not.available");
            FormBuilder.get().addFullWidthAndHeight(new InputNotAvailablePanel(text), parent);
        }
    }

    private void render(MetadataExpectedInputDTO input, DisposablePanel parent) {
        String inputDescription = input.getDescription();
        if (StringUtils.isNotBlank(inputDescription)) {
            JBLabel description = new JBLabel(RendererUtils.htmlText(inputDescription));
            FormBuilder.get().addFullWidthAndHeight(description, parent);
            description.setBorder(JBUI.Borders.empty(5));
        }

        String expectedPayloadTypes = input.getPayload();
        String title = message("metadata.expected.types");
        String text = RendererUtils.htmlTitle(title, expectedPayloadTypes);
        JBLabel expectedType = new JBLabel(text);
        expectedType.setBorder(JBUI.Borders.empty(5));
        FormBuilder.get().addFullWidthAndHeight(expectedType, parent);
    }
}

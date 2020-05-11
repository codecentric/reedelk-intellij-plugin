package com.reedelk.plugin.editor.properties.metadata;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.service.module.impl.component.completion.PresentableTypeUtils;
import com.reedelk.plugin.service.module.impl.component.metadata.ComponentMetadata;
import com.reedelk.plugin.service.module.impl.component.metadata.ComponentMetadataExpectedInput;
import com.reedelk.runtime.api.commons.StringUtils;

import javax.swing.*;
import java.util.Optional;
import java.util.stream.Collectors;

public class MetadataExpectedInput extends AbstractMetadataInputPanel {

    private static final int LEFT_OFFSET = 24;

    public MetadataExpectedInput() {
        setBorder(JBUI.Borders.empty(0, 1, 0, 8));
        setBackground(JBColor.WHITE);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

    @Override
    void render(ComponentMetadata componentMetadata, DisposablePanel parent) {
        Optional<ComponentMetadataExpectedInput> expectedInput = componentMetadata.getExpectedInput();
        if (expectedInput.isPresent()) {
            render(expectedInput.get(), parent);
        } else {
            FormBuilder.get().addFullWidthAndHeight(new DataNotAvailable(), parent);
        }
    }

    private void render(ComponentMetadataExpectedInput input, DisposablePanel parent) {
        String inputDescription = input.getDescription();
        if (StringUtils.isNotBlank(inputDescription)) {
            JBLabel description = new JBLabel(htmlLabel(inputDescription, "", false));
            FormBuilder.get().addFullWidthAndHeight(description, parent);
            description.setBorder(JBUI.Borders.empty(5));
        }

        String expectedPayloadTypes = input.getPayload().stream()
                .map(PresentableTypeUtils::from)
                .collect(Collectors.joining(", "));
        JBLabel expectedType = new JBLabel(htmlLabel("<b style=\"color: #666666\">Expected type: </b>" + expectedPayloadTypes, "", false));
        expectedType.setBorder(JBUI.Borders.empty(5));
        FormBuilder.get().addFullWidthAndHeight(expectedType, parent);
    }
}

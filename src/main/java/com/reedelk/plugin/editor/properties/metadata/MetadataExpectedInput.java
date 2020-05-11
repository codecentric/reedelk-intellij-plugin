package com.reedelk.plugin.editor.properties.metadata;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.editor.properties.commons.DisposableCollapsiblePane;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.service.module.impl.component.metadata.ComponentMetadata;
import com.reedelk.plugin.service.module.impl.component.metadata.ComponentMetadataExpectedInput;
import com.reedelk.runtime.api.commons.StringUtils;

import javax.swing.*;
import java.util.Optional;

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
            DisposableCollapsiblePane description = new DisposableCollapsiblePane(htmlLabel("<b style=\"color: #666666\">description</b>", "", false), () -> {
                JBLabel label = new JBLabel(htmlLabel(inputDescription, "", false));
                label.setBorder(JBUI.Borders.emptyLeft(LEFT_OFFSET));
                return label;
            });
            description.setBorder(JBUI.Borders.empty());
            FormBuilder.get().addFullWidthAndHeight(description, parent);
        }

        String expectedPayloadTypes = String.join(",", input.getPayload());

        DisposableCollapsiblePane description = new DisposableCollapsiblePane(htmlLabel("<b style=\"color: #666666\">payload</b>", "", false), () -> {
            JBLabel label = new JBLabel(htmlLabel(expectedPayloadTypes, "", false));
            label.setBorder(JBUI.Borders.emptyLeft(LEFT_OFFSET));
            return label;
        });
        description.setBorder(JBUI.Borders.empty());
        FormBuilder.get().addFullWidthAndHeight(description, parent);
    }
}

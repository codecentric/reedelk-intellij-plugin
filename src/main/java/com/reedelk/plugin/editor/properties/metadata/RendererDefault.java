package com.reedelk.plugin.editor.properties.metadata;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.editor.properties.commons.DisposableCollapsiblePane;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.service.module.impl.component.metadata.MetadataActualInputDTO;
import com.reedelk.plugin.service.module.impl.component.metadata.MetadataTypeDTO;
import com.reedelk.plugin.service.module.impl.component.metadata.MetadataTypeItemDTO;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

import static com.intellij.util.ui.JBUI.Borders.empty;
import static com.intellij.util.ui.JBUI.Borders.emptyLeft;
import static com.reedelk.plugin.editor.properties.metadata.RendererUtils.*;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;

public class RendererDefault implements Renderer {

    protected static final int LEFT_OFFSET = 24;

    @Override
    public boolean accept(MetadataActualInputDTO actualInputDTO) {
        return actualInputDTO != null &&
                !actualInputDTO.isNotFound() &&
                !actualInputDTO.isMultipleMessages();
    }

    @Override
    public void render(JComponent parent, MetadataActualInputDTO input) {
        String payloadDescription = input.getPayloadDescription();
        if (isNotBlank(payloadDescription)) {
            renderDescription(parent, input.getPayloadDescription());
        }
        List<MetadataTypeDTO> payloads = input.getPayload();
        if (payloads.isEmpty()) {
            renderEmptyPayload(parent);
        } else if (payloads.size() == 1) {
            renderInlinePayload(parent, payloads.get(0));
        } else {
            renderMultiplePayloads(parent, payloads);
        }
        renderAttributes(parent, input);
    }

    private void renderAttributes(JComponent parent, MetadataActualInputDTO input) {
        String title = attributeLabel();
        DisposableCollapsiblePane attributes =
                createCollapsiblePanel(title, input.getAttributes(), false, true);
        FormBuilder.get().addFullWidthAndHeight(attributes, parent);
    }

    private void renderMultiplePayloads(JComponent parent, List<MetadataTypeDTO> payloads) {
        String displayName = payloadLabel("(one of the following types)");
        DisposableCollapsiblePane payload = createPanel(displayName, payloads);
        FormBuilder.get().addFullWidthAndHeight(payload, parent);
    }

    private void renderEmptyPayload(JComponent parent) {
        // TODO: Empty
    }

    private void renderDescription(JComponent parent, String descriptionText) {
        String displayName = descriptionLabel();
        DisposableCollapsiblePane description = new DisposableCollapsiblePane(displayName, () -> {
            JBLabel label = new JBLabel(htmlText(descriptionText));
            label.setBorder(emptyLeft(LEFT_OFFSET));
            return label;
        });
        description.setBorder(empty());
        FormBuilder.get().addFullWidthAndHeight(description, parent);
    }

    private void renderInlinePayload(JComponent parent, MetadataTypeDTO payloadMetadataDTO) {
        String displayName = payloadLabel(payloadMetadataDTO.getDisplayType());
        boolean collapsed = payloadMetadataDTO.getProperties().isEmpty(); // Collapsed if there are no properties
        DisposableCollapsiblePane payload = createCollapsiblePanel(displayName, payloadMetadataDTO, collapsed, true);
        FormBuilder.get().addFullWidthAndHeight(payload, parent);
    }

    private static DisposableCollapsiblePane createPanel(String title, List<MetadataTypeDTO> metadataTypeList) {
        return new DisposableCollapsiblePane(title, () -> {
            DisposablePanel content = new DisposablePanel(new GridBagLayout());
            content.setBackground(JBColor.WHITE);
            metadataTypeList.forEach(metadataType -> {
                JComponent typePanel =
                        createCollapsiblePanel(metadataType);
                FormBuilder.get().addFullWidthAndHeight(typePanel, content);
            });
            return content;
        }, false, true);
    }

    private static JComponent createCollapsiblePanel(MetadataTypeDTO metadataType) {
        String title = htmlText(metadataType.getDisplayType());
        if (metadataType.getProperties().isEmpty()) {
            JBLabel label = new JBLabel(title);
            label.setBorder(emptyLeft(25));
            return label;
        } else {
            JComponent panel = createCollapsiblePanel(title, metadataType, true, false);
            panel.setBorder(emptyLeft(5));
            return panel;
        }
    }

    private static DisposableCollapsiblePane createCollapsiblePanel(String title,
                                                                    MetadataTypeDTO metadataType,
                                                                    boolean defaultCollapsed,
                                                                    boolean horizontalBar) {
        DisposableCollapsiblePane panel = new DisposableCollapsiblePane(title, () -> {
            DisposablePanel content = new DisposablePanel(new GridBagLayout());
            content.setBackground(JBColor.WHITE);
            metadataType.getProperties()
                    .stream()
                    .sorted(Comparator.comparing(ioTypeDTO -> ioTypeDTO.name))
                    .forEach(typeItemDTO -> renderTypeItem(content, typeItemDTO));
            return content;
        }, defaultCollapsed, horizontalBar);
        panel.setBorder(empty());
        return panel;
    }

    private static void renderTypeItem(DisposablePanel content, MetadataTypeItemDTO typeItemDTO) {
        if (isNotBlank(typeItemDTO.displayType)) {
            String label = propertyEntry(typeItemDTO.name, typeItemDTO.displayType);
            JBLabel attributes = new JBLabel(label, JLabel.LEFT);
            attributes.setForeground(Colors.TOOL_WINDOW_PROPERTIES_TEXT);
            attributes.setBorder(emptyLeft(25));
            FormBuilder.get().addLabel(attributes, content);
            FormBuilder.get().addLastField(Box.createHorizontalGlue(), content);
        } else {
            MetadataTypeDTO complex = typeItemDTO.complex;
            String text = typeItemDTO.name + " : " + complex.getDisplayType();
            JComponent payload = createCollapsiblePanel(text, complex, true, false);
            payload.setBorder(emptyLeft(5));
            FormBuilder.get().addFullWidthAndHeight(payload, content);
        }
    }
}

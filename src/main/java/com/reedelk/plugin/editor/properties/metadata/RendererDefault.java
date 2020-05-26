package com.reedelk.plugin.editor.properties.metadata;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.HTMLUtils;
import com.reedelk.plugin.editor.properties.commons.DisposableCollapsiblePane;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.service.module.impl.component.metadata.MetadataActualInputDTO;
import com.reedelk.plugin.service.module.impl.component.metadata.MetadataTypeDTO;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;

import static com.intellij.util.ui.JBUI.Borders.*;
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
        int topOffset = 0;

        String payloadDescription = input.getPayloadDescription();
        if (isNotBlank(payloadDescription)) {
            topOffset = 4;// TODO: ???
            renderDescription(parent, input.getPayloadDescription());
        }

        List<MetadataTypeDTO> payloads = input.getPayload();
        if (payloads.size() == 1) {
            renderInlinePayload(parent, topOffset, payloads.get(0));

        } else if (payloads.isEmpty()) {
            renderEmptyPayload(parent, topOffset);

        } else {
            renderMultiplePayloads(parent, topOffset, payloads);
        }

        String displayName = attributeLabel();
        DisposableCollapsiblePane attributes =
                createCollapsiblePanel(displayName, input.getAttributes(), LEFT_OFFSET, false, true);
        attributes.setBorder(emptyTop(4));
        FormBuilder.get().addFullWidthAndHeight(attributes, parent);
    }

    private void renderMultiplePayloads(JComponent parent, int topOffset, List<MetadataTypeDTO> payloads) {
        String displayName = payloadLabel("(one of the following types)");
        DisposableCollapsiblePane payload = createPanel(displayName, payloads, LEFT_OFFSET, false, true);
        payload.setBorder(emptyTop(topOffset));
        FormBuilder.get().addFullWidthAndHeight(payload, parent);
    }

    private void renderEmptyPayload(JComponent parent, int topOffset) {
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

    private void renderInlinePayload(JComponent parent, int topOffset, MetadataTypeDTO payloadMetadataDTO) {
        String displayName = payloadLabel(payloadMetadataDTO.getDisplayType());
        boolean collapsed = payloadMetadataDTO.getProperties().isEmpty(); // Collapsed if there are no properties
        DisposableCollapsiblePane payload = createCollapsiblePanel(displayName, payloadMetadataDTO, LEFT_OFFSET, collapsed, true);
        payload.setBorder(emptyTop(topOffset));
        FormBuilder.get().addFullWidthAndHeight(payload, parent);
    }

    private static DisposableCollapsiblePane createPanel(String title,
                                                         List<MetadataTypeDTO> metadataTypeList,
                                                         int parentPadding,
                                                         boolean defaultCollapsed,
                                                         boolean horizontalBar) {
        return new DisposableCollapsiblePane(title, () -> {
            DisposablePanel content = new DisposablePanel(new GridBagLayout());
            content.setBackground(JBColor.WHITE);
            metadataTypeList.forEach(metadataType -> {
                String typeTitle = htmlText(metadataType.getDisplayType());
                DisposableCollapsiblePane typePanel =
                        createCollapsiblePanel(typeTitle, metadataType, parentPadding, true, false);
                FormBuilder.get().addFullWidthAndHeight(typePanel, content);
            });
            return content;
        }, defaultCollapsed, horizontalBar);
    }

    private static DisposableCollapsiblePane createCollapsiblePanel(String title,
                                                                    MetadataTypeDTO metadataType,
                                                                    int parentPadding,
                                                                    boolean defaultCollapsed,
                                                                    boolean horizontalBar) {
        return new DisposableCollapsiblePane(title, () -> {
            DisposablePanel content = new DisposablePanel(new GridBagLayout());
            content.setBackground(JBColor.WHITE);
            metadataType.getProperties().stream().sorted(Comparator.comparing(ioTypeDTO -> ioTypeDTO.name)).forEach(iotypeDTO -> {
                if (isNotBlank(iotypeDTO.displayType)) {
                    String label = propertyEntry(iotypeDTO.name, iotypeDTO.displayType);
                    JBLabel attributes = new JBLabel(label, JLabel.LEFT);
                    attributes.setForeground(Colors.TOOL_WINDOW_PROPERTIES_TEXT);
                    attributes.setBorder(emptyLeft(parentPadding));
                    FormBuilder.get().addLabel(attributes, content);
                    FormBuilder.get().addLastField(Box.createHorizontalGlue(), content);

                } else {
                    MetadataTypeDTO complex = iotypeDTO.complex;
                    String titleP = htmlTitle(iotypeDTO.name, HTMLUtils.escape(complex.getDisplayType()));
                    DisposableCollapsiblePane payload = createCollapsiblePanel(titleP, complex, parentPadding, true, false);
                    payload.setBorder(empty());
                    payload.setBorder(emptyLeft(parentPadding - 20));
                    FormBuilder.get().addFullWidthAndHeight(payload, content);
                }
            });

            return content;
        }, defaultCollapsed, horizontalBar);
    }
}

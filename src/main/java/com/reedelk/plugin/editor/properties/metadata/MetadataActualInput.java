package com.reedelk.plugin.editor.properties.metadata;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.HTMLUtils;
import com.reedelk.plugin.editor.properties.commons.DisposableCollapsiblePane;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.service.module.impl.component.metadata.MetadataActualInputDTO;
import com.reedelk.plugin.service.module.impl.component.metadata.MetadataDTO;
import com.reedelk.plugin.service.module.impl.component.metadata.MetadataTypeDTO;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.message.MessageAttributes;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class MetadataActualInput extends AbstractMetadataInputPanel {

    @Override
    void render(MetadataDTO metadataDTO, DisposablePanel parent) {
        Optional<MetadataActualInputDTO> actualInput = metadataDTO.getActualInput();
        if (actualInput.isPresent()) {
            render(actualInput.get(), parent);
        } else {
            FormBuilder.get().addFullWidthAndHeight(new DataNotAvailable(), parent);
        }
    }

    private void render(MetadataActualInputDTO input, DisposablePanel parent) {
        if (input.isMultipleMessages()) {
            JBLabel label = new JBLabel(htmlLabel("Multiple messages: List<Message>", "", false));
            FormBuilder.get().addFullWidthAndHeight(label, parent);
        } else {
            int topOffset = 0;
            if (StringUtils.isNotBlank(input.getPayloadDescription())) {
                DisposableCollapsiblePane description = new DisposableCollapsiblePane(htmlLabel("<b style=\"color: #666666\">description</b>", "", false), () -> {
                    JBLabel label = new JBLabel(htmlLabel(input.getPayloadDescription(), "", false));
                    label.setBorder(JBUI.Borders.emptyLeft(LEFT_OFFSET));
                    return label;
                });
                topOffset = 4;
                description.setBorder(JBUI.Borders.empty());
                FormBuilder.get().addFullWidthAndHeight(description, parent);
            }

            List<MetadataTypeDTO> payloads = input.getPayload();
            if (payloads.size() == 1) {
                // Inline
                MetadataTypeDTO payloadIO = payloads.get(0);
                boolean collapsed = payloadIO.getProperties().isEmpty(); // Collapsed if there are no properties
                DisposableCollapsiblePane payload = createPanel(htmlLabel("<b style=\"color: #666666\">payload</b>", payloadIO.getDisplayType()), payloadIO, LEFT_OFFSET, collapsed, true);
                payload.setBorder(JBUI.Borders.emptyTop(topOffset));
                FormBuilder.get().addFullWidthAndHeight(payload, parent);
            } else {
                DisposableCollapsiblePane payload = createPanel(htmlLabel("<b style=\"color: #666666\">payload</b>", "(one of the following types)"), payloads, LEFT_OFFSET, false, true);
                payload.setBorder(JBUI.Borders.emptyTop(topOffset));
                FormBuilder.get().addFullWidthAndHeight(payload, parent);
            }

            DisposableCollapsiblePane attributes = createPanel(htmlLabel("<b style=\"color: #666666\">attributes</b>", MessageAttributes.class.getSimpleName()), input.getAttributes(), LEFT_OFFSET, false, true);
            attributes.setBorder(JBUI.Borders.emptyTop(4));
            FormBuilder.get().addFullWidthAndHeight(attributes, parent);
        }
    }

    private static DisposableCollapsiblePane createPanel(String title, List<MetadataTypeDTO> payloads, int parentPadding, boolean defaultCollapsed, boolean horizontalBar) {
        return new DisposableCollapsiblePane(title, () -> {
            DisposablePanel content = new DisposablePanel(new GridBagLayout());
            content.setBackground(JBColor.WHITE);
            payloads.forEach(new Consumer<MetadataTypeDTO>() {
                @Override
                public void accept(MetadataTypeDTO descriptor) {
                    String lab = htmlLabel(descriptor.getDisplayType(), "");
                    JBLabel paylodType = new JBLabel(lab, JLabel.LEFT);
                    paylodType.setForeground(Colors.TOOL_WINDOW_PROPERTIES_TEXT);
                    paylodType.setBorder(JBUI.Borders.emptyLeft(parentPadding));
                    FormBuilder.get().addLabel(paylodType, content);
                    FormBuilder.get().addLastField(Box.createHorizontalGlue(), content);

                    descriptor.getProperties().stream().sorted(Comparator.comparing(ioTypeDTO -> ioTypeDTO.name))
                            .forEach(iotypeDTO -> {
                                if (StringUtils.isNotBlank(iotypeDTO.displayType)) {
                                    String label = htmlLabel(iotypeDTO.name, iotypeDTO.displayType);
                                    JBLabel attributes = new JBLabel(label, JLabel.LEFT);
                                    attributes.setForeground(Colors.TOOL_WINDOW_PROPERTIES_TEXT);
                                    attributes.setBorder(JBUI.Borders.emptyLeft(parentPadding));
                                    FormBuilder.get().addLabel(attributes, content);
                                    FormBuilder.get().addLastField(Box.createHorizontalGlue(), content);
                                } else {
                                    MetadataTypeDTO complex = iotypeDTO.complex;
                                    DisposableCollapsiblePane payload = createPanel(htmlLabel(iotypeDTO.name, complex.getDisplayType()), complex, parentPadding, true, horizontalBar);
                                    payload.setBorder(JBUI.Borders.empty());
                                    payload.setBorder(JBUI.Borders.emptyLeft(parentPadding - 20));
                                    FormBuilder.get().addFullWidthAndHeight(payload, content);
                                }
                            });
                }
            });
            return content;
        }, defaultCollapsed, horizontalBar);
    }

    private static DisposableCollapsiblePane createPanel(String title, MetadataTypeDTO descriptor, int parentPadding, boolean defaultCollapsed, boolean horizontalBar) {
        return new DisposableCollapsiblePane(title, () -> {
            DisposablePanel content = new DisposablePanel(new GridBagLayout());
            content.setBackground(JBColor.WHITE);
            descriptor.getProperties().stream().sorted(Comparator.comparing(ioTypeDTO -> ioTypeDTO.name)).forEach(iotypeDTO -> {
                if (StringUtils.isNotBlank(iotypeDTO.displayType)) {
                    String label = htmlLabel(iotypeDTO.name, iotypeDTO.displayType);
                    JBLabel attributes = new JBLabel(label, JLabel.LEFT);
                    attributes.setForeground(Colors.TOOL_WINDOW_PROPERTIES_TEXT);
                    attributes.setBorder(JBUI.Borders.emptyLeft(parentPadding));
                    FormBuilder.get().addLabel(attributes, content);
                    FormBuilder.get().addLastField(Box.createHorizontalGlue(), content);
                } else {
                    MetadataTypeDTO complex = iotypeDTO.complex;
                    DisposableCollapsiblePane payload = createPanel(htmlLabel(iotypeDTO.name, HTMLUtils.escape(complex.getDisplayType())), complex, parentPadding, true, false);
                    payload.setBorder(JBUI.Borders.empty());
                    payload.setBorder(JBUI.Borders.emptyLeft(parentPadding - 20));
                    FormBuilder.get().addFullWidthAndHeight(payload, content);
                }
            });

            return content;
        }, defaultCollapsed, horizontalBar);
    }
}

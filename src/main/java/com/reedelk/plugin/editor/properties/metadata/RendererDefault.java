package com.reedelk.plugin.editor.properties.metadata;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.DisposableCollapsiblePane;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.service.module.impl.component.metadata.MetadataActualInputDTO;
import com.reedelk.plugin.service.module.impl.component.metadata.MetadataTypeDTO;
import com.reedelk.plugin.service.module.impl.component.metadata.MetadataTypeItemDTO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.intellij.util.ui.JBUI.Borders.empty;
import static com.intellij.util.ui.JBUI.Borders.emptyLeft;
import static com.reedelk.plugin.editor.properties.commons.TypeObjectContainerHeader.HorizontalSeparator;
import static com.reedelk.plugin.editor.properties.metadata.RendererUtils.*;
import static com.reedelk.runtime.api.commons.StringUtils.isNotBlank;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.SOUTH;
import static java.util.Comparator.comparing;
import static javax.swing.Box.createHorizontalGlue;

public class RendererDefault implements Renderer {

    protected static final int LEFT_OFFSET = 6;

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
        DisposablePanel content = new DisposablePanel(new GridBagLayout());
        content.setBackground(JBColor.WHITE);
        input.getAttributes()
                .getProperties()
                .stream()
                .sorted(comparing(typeItem -> typeItem.name)) // sort properties by name
                .forEach(metadataTypeItemDTO -> renderTypeItem(content, metadataTypeItemDTO));
        RootItemPanel attributes = new RootItemPanel(title, content);
        FormBuilder.get().addFullWidthAndHeight(attributes, parent);
    }

    private void renderMultiplePayloads(JComponent parent, List<MetadataTypeDTO> payloads) {
        String displayName = payloadLabel("(one of the following types)");
        DisposablePanel content = new DisposablePanel(new GridBagLayout());
        content.setBackground(JBColor.WHITE);
        payloads.forEach(metadataType -> {
            JComponent typePanel = createCollapsiblePanel(metadataType);
            FormBuilder.get().addFullWidthAndHeight(typePanel, content);
        });
        RootItemPanel payload = new RootItemPanel(displayName, content);
        FormBuilder.get().addFullWidthAndHeight(payload, parent);
    }

    private void renderEmptyPayload(JComponent parent) {
        String displayName = payloadLabel("(no info available)");
        JBLabel label = new JBLabel();
        JComponent description = new RootItemPanel(displayName, label);
        FormBuilder.get().addFullWidthAndHeight(description, parent);
    }

    private void renderDescription(JComponent parent, String descriptionText) {
        String displayName = descriptionLabel();
        JBLabel label = new JBLabel(htmlText(descriptionText));
        JComponent description = new RootItemPanel(displayName, label);
        FormBuilder.get().addFullWidthAndHeight(description, parent);
    }

    private void renderInlinePayload(JComponent parent, MetadataTypeDTO payloadMetadataDTO) {
        String displayName = payloadLabel(payloadMetadataDTO.getDisplayType());
        DisposablePanel content = new DisposablePanel(new GridBagLayout());
        content.setBackground(JBColor.WHITE);
        payloadMetadataDTO
                .getProperties()
                .stream()
                .sorted(comparing(typeItem -> typeItem.name)) // sort properties by name
                .forEach(metadataTypeItemDTO -> renderTypeItem(content, metadataTypeItemDTO));
        RootItemPanel payload = new RootItemPanel(displayName, content);
        FormBuilder.get().addFullWidthAndHeight(payload, parent);

    }

    private static JComponent createCollapsiblePanel(MetadataTypeDTO metadataType) {
        String title = htmlText(metadataType.getDisplayType());
        if (metadataType.getProperties().isEmpty()) {
            JBLabel label = new JBLabel(title);
            label.setBorder(emptyLeft(LEFT_OFFSET));
            return label;
        } else {
            JComponent panel = createCollapsiblePanel(title, metadataType);
            panel.setBorder(emptyLeft(LEFT_OFFSET));
            return panel;
        }
    }

    private static DisposableCollapsiblePane createCollapsiblePanel(String title,
                                                                    MetadataTypeDTO metadataType) {
        DisposableCollapsiblePane panel = new DisposableCollapsiblePane(title, () -> {
            DisposablePanel content = new DisposablePanel(new GridBagLayout());
            content.setBackground(JBColor.WHITE);
            metadataType.getProperties()
                    .stream()
                    .sorted(comparing(typeDTO -> typeDTO.name))
                    .forEach(typeItemDTO -> renderTypeItem(content, typeItemDTO));
            return content;
        }, true, false, ClickableLabel.IconAlignment.RIGHT);
        panel.setBorder(empty());
        return panel;
    }

    private static void renderTypeItem(DisposablePanel content, MetadataTypeItemDTO typeItemDTO) {
        if (isNotBlank(typeItemDTO.displayType)) {
            String label = propertyEntry(typeItemDTO.name, typeItemDTO.displayType);
            JBLabel attributes = new JBLabel(label, JLabel.LEFT);
            attributes.setForeground(Colors.TOOL_WINDOW_PROPERTIES_TEXT);
            attributes.setBorder(emptyLeft(LEFT_OFFSET));
            FormBuilder.get().addLabel(attributes, content);
            FormBuilder.get().addLastField(createHorizontalGlue(), content);
        } else {
            MetadataTypeDTO complex = typeItemDTO.complex;
            String text = typeItemDTO.name + " : " + complex.getDisplayType();
            JComponent payload = createCollapsiblePanel(text, complex);
            payload.setBorder(emptyLeft(LEFT_OFFSET));
            FormBuilder.get().addFullWidthAndHeight(payload, content);
        }
    }

    static class RootItemPanel extends DisposablePanel {

        public RootItemPanel(String title, JComponent content) {
            super(new BorderLayout());
            HorizontalSeparator horizontalSeparator = new HorizontalSeparator();
            horizontalSeparator.setOpaque(false);

            setBorder(JBUI.Borders.empty(2, 5));
            setBackground(JBColor.WHITE);
            add(new JBLabel(title), BorderLayout.WEST);
            add(horizontalSeparator, CENTER);
            add(content, SOUTH);
        }
    }
}

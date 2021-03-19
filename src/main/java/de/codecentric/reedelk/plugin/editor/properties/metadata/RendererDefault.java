package de.codecentric.reedelk.plugin.editor.properties.metadata;

import de.codecentric.reedelk.plugin.editor.properties.commons.ClickableLabel;
import de.codecentric.reedelk.plugin.editor.properties.commons.DisposableCollapsiblePane;
import de.codecentric.reedelk.plugin.editor.properties.commons.DisposablePanel;
import de.codecentric.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.MetadataActualInputDTO;
import de.codecentric.reedelk.plugin.service.module.impl.component.metadata.MetadataTypeDTO;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.intellij.util.ui.JBUI.Borders.empty;
import static com.intellij.util.ui.JBUI.Borders.emptyLeft;
import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;
import static de.codecentric.reedelk.runtime.api.commons.StringUtils.isNotBlank;

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
        String title = RendererUtils.attributeLabel();
        JComponent content = new MetadataPanelProperties(input.getAttributes());
        JComponent attributes = new MetadataPanelRootEntry(title, content);
        FormBuilder.get().addFullWidthAndHeight(attributes, parent);
    }

    private void renderMultiplePayloads(JComponent parent, List<MetadataTypeDTO> payloads) {
        String displayName = RendererUtils.payloadLabel(message("metadata.one.of.types"));
        DisposablePanel content = new DisposablePanel(new GridBagLayout());
        content.setBackground(JBColor.WHITE);
        payloads.forEach(metadataType -> {
            JComponent typePanel = createCollapsiblePanel(metadataType);
            FormBuilder.get().addFullWidthAndHeight(typePanel, content);
        });
        JComponent payload = new MetadataPanelRootEntry(displayName, content);
        FormBuilder.get().addFullWidthAndHeight(payload, parent);
    }

    private void renderEmptyPayload(JComponent parent) {
        String displayName = RendererUtils.payloadLabel(message("metadata.no.info.available"));
        JBLabel label = new JBLabel();
        JComponent description = new MetadataPanelRootEntry(displayName, label);
        FormBuilder.get().addFullWidthAndHeight(description, parent);
    }

    private void renderDescription(JComponent parent, String descriptionText) {
        String displayName = RendererUtils.descriptionLabel();
        JBLabel label = new JBLabel(RendererUtils.htmlText(descriptionText));
        JComponent description = new MetadataPanelRootEntry(displayName, label);
        FormBuilder.get().addFullWidthAndHeight(description, parent);
    }

    private void renderInlinePayload(JComponent parent, MetadataTypeDTO payloadMetadataDTO) {
        String displayName = RendererUtils.payloadLabel(payloadMetadataDTO.getDisplayType());
        JComponent content = new MetadataPanelProperties(payloadMetadataDTO);
        JComponent payload = new MetadataPanelRootEntry(displayName, content);
        FormBuilder.get().addFullWidthAndHeight(payload, parent);

    }

    public static JComponent createCollapsiblePanel(MetadataTypeDTO metadataType) {
        String title = RendererUtils.htmlTypeText(metadataType.getDisplayType());
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

    public static DisposableCollapsiblePane createCollapsiblePanel(String title, MetadataTypeDTO metadataType) {
        DisposableCollapsiblePane panel =
                new DisposableCollapsiblePane(title,
                        () -> new MetadataPanelProperties(metadataType), true, false, ClickableLabel.IconAlignment.RIGHT);
        panel.setBorder(empty());
        return panel;
    }
}

package com.reedelk.plugin.editor.properties.metadata;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.DisposableScrollPane;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.service.module.impl.component.metadata.MetadataDTO;

import javax.swing.*;
import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders.empty;
import static com.reedelk.plugin.editor.properties.metadata.RendererUtils.htmlTitle;
import static com.reedelk.plugin.service.module.PlatformModuleService.OnComponentMetadataEvent;

abstract class AbstractMetadataInputPanel extends DisposableScrollPane implements OnComponentMetadataEvent {

    public AbstractMetadataInputPanel() {
        setBorder(empty());
        setBackground(JBColor.WHITE);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

    @Override
    public void onComponentMetadata(MetadataDTO metadataDTO) {
        DisposablePanel theContent = new DisposablePanel(new GridBagLayout());
        theContent.setBackground(JBColor.WHITE);
        DisposablePanel panel = ContainerFactory.pushTop(theContent);
        panel.setBackground(JBColor.WHITE);
        panel.setBorder(empty(5, 2));
        render(metadataDTO, theContent);
        setViewportView(panel);
    }

    @Override
    public void onComponentMetadataError(Exception exception) {
        DisposablePanel theContent = new DisposablePanel(new GridBagLayout());
        theContent.setBackground(JBColor.WHITE);
        DisposablePanel panel = ContainerFactory.pushTop(theContent);
        panel.setBackground(JBColor.WHITE);
        panel.setBorder(empty(5, 2));
        renderError(exception, theContent);
        setViewportView(panel);
    }

    abstract void render(MetadataDTO metadataDTO, DisposablePanel parent);

    public void renderError(Exception exception, DisposablePanel parent) {
        FormBuilder.get().addFullWidthAndHeight(new MetadataError(exception.getMessage()), parent);
    }

    static class MetadataError extends DisposablePanel {
        MetadataError(String errorMessage) {
            super(new BorderLayout());
            JBLabel label =
                    new JBLabel(htmlTitle("An error occurred while fetching metadata for component: " + errorMessage, ""),
                            JLabel.CENTER);
            add(label, BorderLayout.CENTER);
            setBorder(JBUI.Borders.empty(5));
        }
    }
}

package com.reedelk.plugin.editor.properties.metadata;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.DisposableScrollPane;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.service.module.impl.component.metadata.ComponentMetadata;
import com.reedelk.plugin.service.module.impl.component.metadata.OnComponentMetadata;
import com.reedelk.runtime.api.commons.StringUtils;

import javax.swing.*;
import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders.empty;

abstract class AbstractMetadataInputPanel extends DisposableScrollPane implements OnComponentMetadata {

    protected static final int LEFT_OFFSET = 24;

    public AbstractMetadataInputPanel() {
        setBorder(empty());
        setBackground(JBColor.WHITE);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

    @Override
    public void onComponentMetadata(ComponentMetadata componentMetadata) {
        DisposablePanel theContent = new DisposablePanel(new GridBagLayout());
        theContent.setBackground(JBColor.WHITE);
        DisposablePanel panel = ContainerFactory.pushTop(theContent);
        panel.setBackground(JBColor.WHITE);
        panel.setBorder(empty(5, 2));
        render(componentMetadata, theContent);
        setViewportView(panel);
    }

    @Override
    public void onComponentMetadataError(String message) {
        DisposablePanel theContent = new DisposablePanel(new GridBagLayout());
        theContent.setBackground(JBColor.WHITE);
        DisposablePanel panel = ContainerFactory.pushTop(theContent);
        panel.setBackground(JBColor.WHITE);
        panel.setBorder(empty(5, 2));
        renderError(message, theContent);
        setViewportView(panel);
    }

    abstract void render(ComponentMetadata componentMetadata, DisposablePanel parent);


    public void renderError(String message, DisposablePanel parent) {
        FormBuilder.get().addFullWidthAndHeight(new MetadataError(message), parent);
    }

    static class MetadataError extends DisposablePanel {
        MetadataError(String errorMessage) {
            super(new BorderLayout());
            JBLabel label =
                    new JBLabel(htmlLabel("An error occurred while fetching metadata for component: " + errorMessage, ""),
                            JLabel.CENTER);
            add(label, BorderLayout.CENTER);
        }
    }

    protected static class DataNotAvailable extends DisposablePanel {
        DataNotAvailable() {
            super(new BorderLayout());
            add(new JBLabel(htmlLabel("Data is not available, make sure that the previous " +
                    "component define @ComponentOutput annotation.", ""), JLabel.CENTER), BorderLayout.CENTER);
        }
    }


    private static final String HTML_WITH_VALUE = "<html>%s : <i>%s</i></html>";
    private static final String HTML_WITHOUT_VALUE = "<html>%s</html>";

    protected static String htmlLabel(String key, String value) {
        return htmlLabel(key, value, true);
    }

    protected static String htmlLabel(String key, String value, boolean escape) {
        if (StringUtils.isBlank(value)) {
            if (escape) {
                key = key.replaceAll("<", "&lt;");
                key = key.replaceAll(">", "&gt;");
            }
            return String.format(HTML_WITHOUT_VALUE, key);
        } else {
            if (escape) {
                value = value.replaceAll("<", "&lt;");
                value = value.replaceAll(">", "&gt;");
            }
            return String.format(HTML_WITH_VALUE, key, value);
        }
    }
}

package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.Topics;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.plugin.service.module.CompletionService;
import com.reedelk.plugin.service.module.impl.completion.ComponentIO;
import com.reedelk.plugin.service.module.impl.completion.OnComponentIO;
import com.reedelk.runtime.api.commons.ImmutableMap;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.Map;

import static com.intellij.util.ui.JBUI.Borders.customLine;
import static com.intellij.util.ui.JBUI.Borders.emptyLeft;
import static com.reedelk.plugin.commons.Colors.SCRIPT_EDITOR_CONTEXT_PANEL_TITLE_BG;

public class PropertiesPanelIOContainer extends DisposablePanel implements OnComponentIO {

    private final DisposablePanel loadingPanel;
    private MessageBusConnection connection;

    public PropertiesPanelIOContainer(Module module, FlowSnapshot snapshot, String componentFullyQualifiedName) {
        super(new BorderLayout());

        loadingPanel = new PanelWithText.LoadingContentPanel();
        loadingPanel.setOpaque(true);
        loadingPanel.setBackground(JBColor.WHITE);

        add(new HeaderPanel("Input Message"), BorderLayout.NORTH);
        add(loadingPanel, BorderLayout.CENTER);
        setBackground(JBColor.WHITE);

        this.connection = module.getMessageBus().connect(this);
        this.connection.subscribe(Topics.ON_COMPONENT_IO, this);

        CompletionService.getInstance(module)
                .loadComponentIO(componentFullyQualifiedName, componentFullyQualifiedName);
    }

    @Override
    public void onComponentIO(String inputFQCN, String outputFQCN, ComponentIO componentIO) {
        ContentPanel contentPanel = new ContentPanel();
        DisposablePanel panel = ContainerFactory.pushTop(contentPanel);
        panel.setBackground(JBColor.WHITE);
        remove(loadingPanel);
        add(panel, BorderLayout.CENTER);

        DisposableCollapsiblePane attributes = createPanel(htmlLabel("attributes", "Map"), componentIO.getAttributes());
        FormBuilder.get().addFullWidthAndHeight(attributes, contentPanel);

        DisposableCollapsiblePane payload = createPanel(htmlLabel("payload", "byte[]"), componentIO.getPayload());
        FormBuilder.get().addFullWidthAndHeight(payload, contentPanel);

        ApplicationManager.getApplication().invokeLater(this::repaint);
    }

    @Override
    public void onComponentIONotFound(String inputFQCN, String outputFQCN) {
        ContentPanel contentPanel = new ContentPanel();
        DisposablePanel panel = ContainerFactory.pushTop(contentPanel);
        panel.setBackground(JBColor.WHITE);
        remove(loadingPanel);
        add(panel, BorderLayout.CENTER);

        DisposableCollapsiblePane attributes = createPanel(htmlLabel("attributes", "Map"), ImmutableMap.of());
        FormBuilder.get().addFullWidthAndHeight(attributes, contentPanel);

        DisposableCollapsiblePane payload = createPanel(htmlLabel("payload", "byte[]"), ImmutableMap.of());
        FormBuilder.get().addFullWidthAndHeight(payload, contentPanel);

        ApplicationManager.getApplication().invokeLater(this::repaint);
    }

    @Override
    public void dispose() {
        super.dispose();
        this.connection.disconnect();
        this.connection = null;
    }

    static class HeaderPanel extends DisposablePanel {
        public HeaderPanel(String text) {
            super(new BorderLayout());
            setBackground(SCRIPT_EDITOR_CONTEXT_PANEL_TITLE_BG);

            JLabel label = new JLabel(text, SwingConstants.LEFT);
            //label.setFont(label.getFont().deriveFont(Font, label.getFont().getSize()));
            label.setForeground(JBColor.DARK_GRAY);
            add(label, BorderLayout.WEST);
            setPreferredSize(new Dimension(200, 25));

            Border inside = emptyLeft(10);
            Border outside = customLine(JBColor.LIGHT_GRAY, 0, 0, 1, 0);
            setBorder(new CompoundBorder(outside, inside));
        }
    }


    static class ContentPanel extends DisposablePanel {
        public ContentPanel() {
            super(new GridBagLayout());
            setBorder(JBUI.Borders.empty(0, 1, 0, 8));
            setBackground(JBColor.WHITE);
        }
    }

    private static DisposableCollapsiblePane createPanel(String title, Map<String, ComponentIO.IOTypeDescriptor> keyAndValue) {
        return new DisposableCollapsiblePane(title, () -> {
            DisposablePanel content = new DisposablePanel(new GridBagLayout());
            content.setBackground(JBColor.WHITE);
            keyAndValue.forEach((key, value) -> {
                if (value.isNameOnly()) {
                    String label = htmlLabel(key, value.getName());
                    JBLabel attributes = new JBLabel(label, JLabel.LEFT);
                    attributes.setForeground(Colors.TOOL_WINDOW_PROPERTIES_TEXT);
                    attributes.setBorder(JBUI.Borders.empty());
                    FormBuilder.get().addLabel(attributes, content);
                    FormBuilder.get().addLastField(Box.createHorizontalGlue(), content);
                }
            });
            return content;
        });
    }

    private static final String HTML = "<html>%s : <i>%s</i></html>";

    private static String htmlLabel(String key, String value) {
        return String.format(HTML, key, value);
    }
}

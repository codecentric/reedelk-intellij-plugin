package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.ui.AncestorListenerAdapter;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.commons.Topics;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.service.module.ComponentService;
import com.reedelk.plugin.service.module.impl.component.completion.ComponentIO;
import com.reedelk.plugin.service.module.impl.component.completion.OnComponentIO;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.AncestorEvent;
import java.awt.*;
import java.util.List;

import static com.intellij.util.ui.JBUI.Borders.customLine;
import static com.intellij.util.ui.JBUI.Borders.emptyLeft;
import static com.reedelk.plugin.commons.Colors.SCRIPT_EDITOR_CONTEXT_PANEL_TITLE_BG;

public class PropertiesPanelIOContainer extends DisposablePanel implements OnComponentIO {

    private final DisposablePanel loadingPanel;
    private MessageBusConnection connection;

    public PropertiesPanelIOContainer(Module module, ContainerContext context, String componentFullyQualifiedName) {
        super(new BorderLayout());

        loadingPanel = new PanelWithText.LoadingContentPanel();
        loadingPanel.setOpaque(true);
        loadingPanel.setBackground(JBColor.WHITE);

        add(new HeaderPanel("Input Message"), BorderLayout.NORTH);
        add(loadingPanel, BorderLayout.CENTER);
        setBackground(JBColor.WHITE);

        this.connection = module.getMessageBus().connect();
        this.connection.subscribe(Topics.ON_COMPONENT_IO, this);

        addAncestorListener(new AncestorListenerAdapter() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                ComponentService.getInstance(module)
                        .inputOutputOf(context, componentFullyQualifiedName);
            }
        });
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

        List<ComponentIO.OutputDescriptor> payloads = componentIO.getPayload();
        if (payloads.size() == 1) {
            ComponentIO.OutputDescriptor outputDescriptor = payloads.get(0);
            DisposableCollapsiblePane payload = createPanel(htmlLabel("payload", outputDescriptor.getType()), outputDescriptor);
            FormBuilder.get().addFullWidthAndHeight(payload, contentPanel);
        } else {
            // TODO: Handle mutlple types
        }



        ApplicationManager.getApplication().invokeLater(this::repaint);
    }

    @Override
    public void dispose() {
        super.dispose();
        DisposableUtils.dispose(connection);
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

    private static DisposableCollapsiblePane createPanel(String title, ComponentIO.OutputDescriptor descriptor) {
        return new DisposableCollapsiblePane(title, () -> {
            DisposablePanel content = new DisposablePanel(new GridBagLayout());
            content.setBackground(JBColor.WHITE);
            descriptor.getProperties().forEach(suggestion -> {
                String label = htmlLabel(suggestion.lookupString(), suggestion.presentableType());
                JBLabel attributes = new JBLabel(label, JLabel.LEFT);
                attributes.setForeground(Colors.TOOL_WINDOW_PROPERTIES_TEXT);
                attributes.setBorder(JBUI.Borders.empty());
                FormBuilder.get().addLabel(attributes, content);
                FormBuilder.get().addLastField(Box.createHorizontalGlue(), content);
            });

            return content;
        });
    }

    private static final String HTML = "<html>%s : <i>%s</i></html>";

    private static String htmlLabel(String key, String value) {
        return String.format(HTML, key, value);
    }
}

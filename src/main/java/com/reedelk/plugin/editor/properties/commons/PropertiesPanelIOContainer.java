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
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.message.MessageAttributes;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.AncestorEvent;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

import static com.intellij.util.ui.JBUI.Borders.customLine;
import static com.intellij.util.ui.JBUI.Borders.emptyLeft;
import static com.reedelk.plugin.commons.Colors.SCRIPT_EDITOR_CONTEXT_PANEL_TITLE_BG;

public class PropertiesPanelIOContainer extends DisposablePanel implements OnComponentIO {

    private final DisposablePanel loadingPanel;
    private MessageBusConnection connection;

    public PropertiesPanelIOContainer(@NotNull Module module, ContainerContext context, String componentFullyQualifiedName) {
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
        DisposableScrollPane contentPanel = new ContentPanel();
        contentPanel.setBackground(JBColor.WHITE);

        DisposablePanel theContent = new DisposablePanel(new GridBagLayout());
        theContent.setBackground(JBColor.WHITE);
        DisposablePanel panel = ContainerFactory.pushTop(theContent);
        panel.setBackground(JBColor.WHITE);
        panel.setBorder(JBUI.Borders.empty(5, 2));
        contentPanel.setViewportView(panel);

        List<ComponentIO.IOTypeDescriptor> payloads = componentIO.getPayload();
        if (payloads.size() == 1) {
            // Inline
            ComponentIO.IOTypeDescriptor IOTypeDescriptor = payloads.get(0);
            boolean collapsed = IOTypeDescriptor.getProperties().isEmpty(); // Collapsed if there are no properties
            DisposableCollapsiblePane payload = createPanel(htmlLabel("payload", IOTypeDescriptor.getType()), IOTypeDescriptor, 30, collapsed, true);
            payload.setBorder(JBUI.Borders.empty());
            FormBuilder.get().addFullWidthAndHeight(payload, theContent);
        } else {
            DisposableCollapsiblePane payload = createPanel(htmlLabel("payload", "(one of the following types)"), payloads, 30, false, true);
            payload.setBorder(JBUI.Borders.empty());
            FormBuilder.get().addFullWidthAndHeight(payload, theContent);
        }

        DisposableCollapsiblePane attributes = createPanel(htmlLabel("attributes", MessageAttributes.class.getSimpleName()), componentIO.getAttributes(), 30, false, true);
        attributes.setBorder(JBUI.Borders.emptyTop(4));
        FormBuilder.get().addFullWidthAndHeight(attributes, theContent);

        ApplicationManager.getApplication().invokeLater(() -> {
            remove(loadingPanel);
            add(contentPanel, BorderLayout.CENTER);
            repaint();
        });
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
            label.setForeground(JBColor.DARK_GRAY);
            add(label, BorderLayout.WEST);
            setPreferredSize(new Dimension(200, 25));

            Border inside = emptyLeft(10);
            Border outside = customLine(JBColor.LIGHT_GRAY, 0, 0, 1, 0);
            setBorder(new CompoundBorder(outside, inside));
        }
    }


    static class ContentPanel extends DisposableScrollPane {
        public ContentPanel() {
            setBorder(JBUI.Borders.empty(0, 1, 0, 8));
            setBackground(JBColor.WHITE);
        }
    }

    private static DisposableCollapsiblePane createPanel(String title, List<ComponentIO.IOTypeDescriptor> payloads, int parentPadding, boolean defaultCollapsed, boolean horizontalBar) {
        return new DisposableCollapsiblePane(title, () -> {
            DisposablePanel content = new DisposablePanel(new GridBagLayout());
            content.setBackground(JBColor.WHITE);
            payloads.forEach(new Consumer<ComponentIO.IOTypeDescriptor>() {
                @Override
                public void accept(ComponentIO.IOTypeDescriptor descriptor) {
                    String lab = htmlLabel(descriptor.getType(), "");
                    JBLabel paylodType = new JBLabel(lab, JLabel.LEFT);
                    paylodType.setForeground(Colors.TOOL_WINDOW_PROPERTIES_TEXT);
                    paylodType.setBorder(JBUI.Borders.emptyLeft(parentPadding));
                    FormBuilder.get().addLabel(paylodType, content);
                    FormBuilder.get().addLastField(Box.createHorizontalGlue(), content);

                    descriptor.getProperties().stream().sorted(Comparator.comparing(ioTypeDTO -> ioTypeDTO.name))
                            .forEach(iotypeDTO -> {
                                if (StringUtils.isNotBlank(iotypeDTO.value)) {
                                    String label = htmlLabel(iotypeDTO.name, iotypeDTO.value);
                                    JBLabel attributes = new JBLabel(label, JLabel.LEFT);
                                    attributes.setForeground(Colors.TOOL_WINDOW_PROPERTIES_TEXT);
                                    attributes.setBorder(JBUI.Borders.emptyLeft(parentPadding));
                                    FormBuilder.get().addLabel(attributes, content);
                                    FormBuilder.get().addLastField(Box.createHorizontalGlue(), content);
                                } else {
                                    ComponentIO.IOTypeDescriptor complex = iotypeDTO.complex;
                                    DisposableCollapsiblePane payload = createPanel(htmlLabel(iotypeDTO.name, complex.getType()), complex, parentPadding, true, horizontalBar);
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

    private static DisposableCollapsiblePane createPanel(String title, ComponentIO.IOTypeDescriptor descriptor, int parentPadding, boolean defaultCollapsed, boolean horizontalBar) {
        return new DisposableCollapsiblePane(title, () -> {
            DisposablePanel content = new DisposablePanel(new GridBagLayout());
            content.setBackground(JBColor.WHITE);
            descriptor.getProperties().stream().sorted(Comparator.comparing(ioTypeDTO -> ioTypeDTO.name)).forEach(iotypeDTO -> {
                if (StringUtils.isNotBlank(iotypeDTO.value)) {
                    String label = htmlLabel(iotypeDTO.name, iotypeDTO.value);
                    JBLabel attributes = new JBLabel(label, JLabel.LEFT);
                    attributes.setForeground(Colors.TOOL_WINDOW_PROPERTIES_TEXT);
                    attributes.setBorder(JBUI.Borders.emptyLeft(parentPadding));
                    FormBuilder.get().addLabel(attributes, content);
                    FormBuilder.get().addLastField(Box.createHorizontalGlue(), content);
                } else {
                    ComponentIO.IOTypeDescriptor complex = iotypeDTO.complex;
                    DisposableCollapsiblePane payload = createPanel(htmlLabel(iotypeDTO.name, ""), complex, parentPadding, true, false);
                    payload.setBorder(JBUI.Borders.empty());
                    payload.setBorder(JBUI.Borders.emptyLeft(parentPadding - 20));
                    FormBuilder.get().addFullWidthAndHeight(payload, content);
                }
            });

            return content;
        }, defaultCollapsed, horizontalBar);
    }

    private static final String HTML_WITH_VALUE = "<html>%s : <i>%s</i></html>";
    private static final String HTML_WITHOUT_VALUE = "<html>%s</html>";

    private static String htmlLabel(String key, String value) {
        if (StringUtils.isBlank(value)) {
            key = key.replaceAll("<", "&lt;");
            key = key.replaceAll(">", "&gt;");
            return String.format(HTML_WITHOUT_VALUE, key);
        } else {
            value = value.replaceAll("<", "&lt;");
            value = value.replaceAll(">", "&gt;");
            return String.format(HTML_WITH_VALUE, key, value);
        }
    }
}

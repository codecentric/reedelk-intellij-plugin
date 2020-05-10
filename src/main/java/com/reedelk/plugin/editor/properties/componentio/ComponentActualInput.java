package com.reedelk.plugin.editor.properties.componentio;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.commons.Colors;
import com.reedelk.plugin.editor.properties.commons.*;
import com.reedelk.plugin.service.module.impl.component.componentio.IOComponent;
import com.reedelk.plugin.service.module.impl.component.componentio.IOTypeDescriptor;
import com.reedelk.plugin.service.module.impl.component.componentio.OnComponentIO;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.api.message.MessageAttributes;

import javax.swing.*;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class ComponentActualInput extends DisposableScrollPane implements OnComponentIO {

    private static final int LEFT_OFFSET = 24;

    public ComponentActualInput() {
        setBorder(JBUI.Borders.empty(0, 1, 0, 8));
        setBackground(JBColor.WHITE);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    }

    @Override
    public void onComponentIO(String inputFQCN, String outputFQCN, IOComponent IOComponent) {
        DisposablePanel theContent = new DisposablePanel(new GridBagLayout());
        theContent.setBackground(JBColor.WHITE);
        DisposablePanel panel = ContainerFactory.pushTop(theContent);
        panel.setBackground(JBColor.WHITE);
        panel.setBorder(JBUI.Borders.empty(5, 2));
        setViewportView(panel);

        int topOffset = 0;
        if (StringUtils.isNotBlank(IOComponent.getPayloadDescription())) {
            DisposableCollapsiblePane description = new DisposableCollapsiblePane(htmlLabel("<b style=\"color: #666666\">description</b>", "", false), () -> {
                JBLabel label = new JBLabel(htmlLabel(IOComponent.getPayloadDescription(), "", false));
                label.setBorder(JBUI.Borders.emptyLeft(LEFT_OFFSET));
                return label;
            });
            topOffset = 4;
            description.setBorder(JBUI.Borders.empty());
            FormBuilder.get().addFullWidthAndHeight(description, theContent);
        }

        List<IOTypeDescriptor> payloads = IOComponent.getPayload();
        if (payloads.size() == 1) {
            // Inline
            IOTypeDescriptor payloadIO = payloads.get(0);
            boolean collapsed = payloadIO.getProperties().isEmpty(); // Collapsed if there are no properties
            DisposableCollapsiblePane payload = createPanel(htmlLabel("<b style=\"color: #666666\">payload</b>", payloadIO.getType()), payloadIO, LEFT_OFFSET, collapsed, true);
            payload.setBorder(JBUI.Borders.emptyTop(topOffset));
            FormBuilder.get().addFullWidthAndHeight(payload, theContent);
        } else {
            DisposableCollapsiblePane payload = createPanel(htmlLabel("<b style=\"color: #666666\">payload</b>", "(one of the following types)"), payloads, LEFT_OFFSET, false, true);
            payload.setBorder(JBUI.Borders.emptyTop(topOffset));
            FormBuilder.get().addFullWidthAndHeight(payload, theContent);
        }

        DisposableCollapsiblePane attributes = createPanel(htmlLabel("<b style=\"color: #666666\">attributes</b>", MessageAttributes.class.getSimpleName()), IOComponent.getAttributes(), LEFT_OFFSET, false, true);
        attributes.setBorder(JBUI.Borders.emptyTop(4));
        FormBuilder.get().addFullWidthAndHeight(attributes, theContent);
    }

    private static DisposableCollapsiblePane createPanel(String title, List<IOTypeDescriptor> payloads, int parentPadding, boolean defaultCollapsed, boolean horizontalBar) {
        return new DisposableCollapsiblePane(title, () -> {
            DisposablePanel content = new DisposablePanel(new GridBagLayout());
            content.setBackground(JBColor.WHITE);
            payloads.forEach(new Consumer<IOTypeDescriptor>() {
                @Override
                public void accept(IOTypeDescriptor descriptor) {
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
                                    IOTypeDescriptor complex = iotypeDTO.complex;
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

    private static DisposableCollapsiblePane createPanel(String title, IOTypeDescriptor descriptor, int parentPadding, boolean defaultCollapsed, boolean horizontalBar) {
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
                    IOTypeDescriptor complex = iotypeDTO.complex;
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
        return htmlLabel(key, value, true);
    }

    private static String htmlLabel(String key, String value, boolean escape) {
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

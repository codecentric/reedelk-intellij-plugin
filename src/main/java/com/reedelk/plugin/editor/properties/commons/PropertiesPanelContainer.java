package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.module.Module;
import com.intellij.ui.JBColor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.commons.Sizes;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.graph.FlowSnapshot;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.intellij.util.ui.JBUI.Borders.customLine;

public class PropertiesPanelContainer extends DisposableThreeComponentsSplitter {

    private static final boolean HORIZONTAL = false;

    private static final String CONTENT_TYPE = "text/html";
    private static final String HTML_TEMPLATE =
            "<div style=\"color: #333333; padding-left:5px;padding-right:10px;font-family:verdana\">" +
                    "<h2>%s</h2>" +
                    "<p>%s</p>" +
                    "</div>";

    private PropertiesPanelContainer(PropertiesPanelTabbedPanel properties) {
        super(HORIZONTAL);

        Border outside = customLine(JBColor.WHITE, 0, 0, 0, 3);
        Border inside = customLine(JBColor.LIGHT_GRAY, 0, 0, 0, 1);
        Border border = new CompoundBorder(outside, inside);
        properties.setBorder(border);

        JEditorPane componentOutputData = new JEditorPane();
        componentOutputData.setContentType(CONTENT_TYPE);
        componentOutputData.setText(String.format(HTML_TEMPLATE, "Output", "Message Input"));

        setLastSize(Sizes.PropertiesPane.COMPONENT_OUTPUT_INFO_WIDTH);
        setInnerComponent(properties);
        setLastComponent(componentOutputData);
        setDividerWidth(0);
        setDividerMouseZoneSize(10);
    }

    public PropertiesPanelContainer(@NotNull Module module,
                                    @NotNull FlowSnapshot flowSnapshot,
                                    @NotNull ComponentData componentData,
                                    @NotNull Map<String, List<PropertyDescriptor>> propertiesByGroup,
                                    @NotNull ContainerContext context) {
        this(new PropertiesPanelTabbedPanel(module, flowSnapshot, componentData, propertiesByGroup, context));
    }

    public PropertiesPanelContainer(@NotNull ComponentData componentData,
                                    @NotNull Map<String, Supplier<JComponent>> tabAndComponentSupplier,
                                    @NotNull ContainerContext context) {
        this(new PropertiesPanelTabbedPanel(componentData, tabAndComponentSupplier, context));
    }
}

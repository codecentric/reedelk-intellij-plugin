package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.module.Module;
import com.intellij.ui.JBColor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.commons.DisposableUtils;
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
    private ContainerContext context;

    private PropertiesPanelContainer(@NotNull Module module,
                                     @NotNull ContainerContext context,
                                     @NotNull String componentFullyQualifiedName,
                                     @NotNull PropertiesPanelTabbedPanel properties) {
        super(HORIZONTAL);
        this.context = context;

        Border outside = customLine(JBColor.WHITE, 0, 0, 0, 0);
        Border inside = customLine(JBColor.LIGHT_GRAY, 0, 0, 0, 1);
        Border border = new CompoundBorder(outside, inside);
        properties.setBorder(border);

        PropertiesPanelIOContainer componentIO = new PropertiesPanelIOContainer(module, componentFullyQualifiedName);

        setLastSize(Sizes.PropertiesPane.COMPONENT_OUTPUT_INFO_WIDTH);
        setInnerComponent(properties);
        setLastComponent(componentIO);
        setDividerWidth(0);
        setDividerMouseZoneSize(10);
    }

    public PropertiesPanelContainer(@NotNull Module module,
                                    @NotNull FlowSnapshot flowSnapshot,
                                    @NotNull ComponentData componentData,
                                    @NotNull Map<String, List<PropertyDescriptor>> propertiesByGroup,
                                    @NotNull ContainerContext context) {
        this(module, context, componentData.getFullyQualifiedName(),
                new PropertiesPanelTabbedPanel(module, flowSnapshot, componentData, propertiesByGroup, context));
    }

    public PropertiesPanelContainer(@NotNull Module module,
                                    @NotNull ComponentData componentData,
                                    @NotNull Map<String, Supplier<JComponent>> tabAndComponentSupplier,
                                    @NotNull ContainerContext context) {
        this(module, context, componentData.getFullyQualifiedName(),
                new PropertiesPanelTabbedPanel(componentData, tabAndComponentSupplier, context));
    }


    @Override
    public void dispose() {
        super.dispose();
        DisposableUtils.dispose(context);
        this.context = null;
    }
}

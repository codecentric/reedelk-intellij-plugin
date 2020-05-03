package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentDataHolder;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyTypeDescriptor;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.editor.properties.context.PropertyAccessor;
import com.reedelk.plugin.editor.properties.renderer.PropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.PropertyTypeRendererFactory;
import com.reedelk.plugin.graph.FlowSnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.util.ui.JBUI.Borders.empty;
import static java.util.Collections.emptyList;

public class PropertiesPanelHolder extends DisposablePanel {

    private static final Border PANEL_BORDERS = empty(8, 0, 0, 10);

    private final transient Module module;
    private final ContainerContext context;
    private final transient FlowSnapshot snapshot;
    private final transient ComponentDataHolder dataHolder;
    private final transient List<PropertyDescriptor> descriptors = new ArrayList<>();

    public PropertiesPanelHolder(@NotNull Module module,
                                 @NotNull ContainerContext context,
                                 @NotNull ComponentDataHolder dataHolder,
                                 @NotNull List<PropertyDescriptor> descriptors,
                                 @Nullable FlowSnapshot snapshot) {
        this.module = module;
        this.context = context;
        this.snapshot = snapshot;
        this.dataHolder = dataHolder;
        this.descriptors.addAll(descriptors);

        setLayout(new GridBagLayout());
        setBorder(PANEL_BORDERS);
        initAccessors();
        renderProperties();
    }

    /**
     * Used by router component. The only property rendered by the router is the condition route table which
     * is a special, custom rendered property type.
     */
    public PropertiesPanelHolder(@NotNull Module module,
                                 @NotNull ContainerContext context,
                                 @NotNull ComponentDataHolder dataHolder,
                                 @Nullable FlowSnapshot snapshot) {
        this(module, context, dataHolder, emptyList(), snapshot);
    }

    /**
     * Constructor used by a configuration panel dialog. The configuration panel dialog does not
     * immediately change the values on the Graph snapshot since it writes the values in a config file only when
     * the 'OK' button is pressed.
     */
    public PropertiesPanelHolder(@NotNull Module module,
                                 @NotNull ContainerContext context,
                                 @NotNull ComponentDataHolder dataHolder,
                                 @NotNull List<PropertyDescriptor> descriptors) {
        this(module, context, dataHolder, descriptors, null);
    }

    private void initAccessors() {
        descriptors.forEach(descriptor ->
                context.getPropertyAccessor(descriptor, snapshot, dataHolder));
    }

    private void renderProperties() {

        descriptors.forEach(descriptor -> {

            PropertyTypeDescriptor propertyType = descriptor.getType();

            PropertyTypeRenderer renderer = PropertyTypeRendererFactory.get().from(propertyType);

            PropertyAccessor propertyAccessor = context.getPropertyAccessor(descriptor, snapshot, dataHolder);

            JComponent renderedComponent = renderer.render(module, descriptor, propertyAccessor, context);

            renderer.addToParent(this, renderedComponent, descriptor, context);

        });
    }
}

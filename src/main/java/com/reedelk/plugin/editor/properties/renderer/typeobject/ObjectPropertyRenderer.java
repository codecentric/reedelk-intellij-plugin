package com.reedelk.plugin.editor.properties.renderer.typeobject;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.*;
import com.reedelk.plugin.commons.TooltipContent;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.reedelk.plugin.editor.properties.commons.*;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.runtime.commons.JsonParser;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;


public class ObjectPropertyRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor descriptor,
                             @NotNull PropertyAccessor accessor,
                             @NotNull ContainerContext context) {
        TypeObjectDescriptor objectDescriptor = descriptor.getType();
        return Shared.YES.equals(objectDescriptor.getShared()) ?
                renderShareable(module, descriptor, accessor, context) :
                renderInline(module, accessor, descriptor);
    }

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull PropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {
        TypeObjectDescriptor objectDescriptor = descriptor.getType();
        if (Shared.NO.equals(objectDescriptor.getShared())) {
            addToParentInline(parent, rendered, descriptor, context);
        } else {
            super.addToParent(parent, rendered, descriptor, context);
        }
        // Add the component to the context
        context.addComponent(new JComponentHolder(rendered));
    }

    @NotNull
    private JComponent renderInline(Module module, PropertyAccessor propertyAccessor, PropertyDescriptor descriptor) {
        TypeObjectDescriptor objectDescriptor = descriptor.getType();
        TooltipContent tooltipContent = TooltipContent.from(descriptor);
        if (Collapsible.YES.equals(objectDescriptor.getCollapsible())) {
            // Deferred rendering (only when it is un-collapsed)
            return new CollapsibleObjectTypeContainer(
                    descriptor.getDisplayName(),
                    tooltipContent,
                    () -> renderObjectProperties(propertyAccessor, objectDescriptor, module));
        } else {
            JComponent propertiesPanel = renderObjectProperties(propertyAccessor, objectDescriptor, module);
            // If the property type is a complex object (not shared), we wrap it in a bordered box with title
            // the name of the object property.
            return ContainerFactory.createObjectTypeContainer(propertiesPanel, descriptor.getDisplayName(), tooltipContent);
        }
    }

    @NotNull
    private JComponent renderShareable(Module module, PropertyDescriptor descriptor, PropertyAccessor propertyAccessor, ContainerContext context) {
        ComponentDataHolder dataHolder = propertyAccessor.get();
        FlowSnapshot snapshot = propertyAccessor.getSnapshot();

        // We create the accessor for the config reference:
        // a shareable config object is referenced with a unique UUID
        PropertyAccessor refAccessor = PropertyAccessorFactory.get()
                .typeDescriptor(descriptor.getType())
                .propertyName(JsonParser.Component.ref())
                .snapshot(snapshot)
                .dataHolder(dataHolder)
                .build();

        return new ShareableConfigInputField(module, dataHolder, descriptor, refAccessor, context);
    }

    private void addToParentInline(@NotNull JComponent parent, @NotNull JComponent rendered, @NotNull PropertyDescriptor descriptor, @NotNull ContainerContext context) {
        // If the property has any 'when' condition, we apply listener/s to make it
        // visible (or not) when the condition is met (or not).
        applyWhenVisibility(descriptor.getWhens(), context, rendered);

        // Add the component to the parent container.
        FormBuilder.get().addFullWidthAndHeight(rendered, parent);
    }

    @NotNull
    private JComponent renderObjectProperties(PropertyAccessor propertyAccessor, TypeObjectDescriptor objectDescriptor, Module module) {
        // The accessor of type object returns a TypeObject map.
        ComponentDataHolder dataHolder = propertyAccessor.get();

        FlowSnapshot snapshot = propertyAccessor.getSnapshot();

        List<PropertyDescriptor> objectProperties = objectDescriptor.getObjectProperties();

        String typeFullyQualifiedName = objectDescriptor.getTypeFullyQualifiedName();

        return new PropertiesPanelHolder(module, typeFullyQualifiedName, dataHolder, objectProperties, snapshot);
    }
}

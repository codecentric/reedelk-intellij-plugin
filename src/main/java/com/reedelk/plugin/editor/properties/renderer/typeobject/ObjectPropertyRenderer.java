package com.reedelk.plugin.editor.properties.renderer.typeobject;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.*;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.reedelk.plugin.editor.properties.commons.*;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.PropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.PropertyTypeRendererFactory;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.runtime.commons.JsonParser;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

import static com.reedelk.plugin.component.domain.Shared.NO;
import static com.reedelk.plugin.component.domain.Shared.YES;

public class ObjectPropertyRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull ComponentPropertyDescriptor descriptor,
                             @NotNull PropertyAccessor accessor,
                             @NotNull ContainerContext context) {
        TypeObjectDescriptor objectDescriptor = descriptor.getPropertyType();
        return YES.equals(objectDescriptor.getShared()) ?
                renderShareable(module, descriptor, accessor, context) :
                renderInline(module, accessor, objectDescriptor);
    }

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull ComponentPropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {
        TypeObjectDescriptor objectDescriptor = descriptor.getPropertyType();
        if (NO.equals(objectDescriptor.getShared())) {
            addToParentInline(parent, rendered, descriptor, context);
        } else {
            super.addToParent(parent, rendered, descriptor, context);
        }
        // Add the component to the context
        context.addComponent(new JComponentHolder(rendered));
    }

    @NotNull
    private JComponent renderInline(Module module, PropertyAccessor propertyAccessor, TypeObjectDescriptor objectDescriptor) {
        // The accessor of type object returns a TypeObject map.
        ComponentDataHolder dataHolder = propertyAccessor.get();

        FlowSnapshot snapshot = propertyAccessor.getSnapshot();

        List<ComponentPropertyDescriptor> objectProperties = objectDescriptor.getObjectProperties();

        PropertiesPanelHolder propertiesPanel = new PropertiesPanelHolder(
                objectDescriptor.getTypeFullyQualifiedName(), dataHolder, objectProperties, snapshot);

        objectProperties.forEach(objectProperty -> {

            String propertyName = objectProperty.getPropertyName();

            PropertyAccessor nestedPropertyAccessor = propertiesPanel.getAccessor(propertyName);

            TypeDescriptor propertyType = objectProperty.getPropertyType();

            PropertyTypeRenderer renderer = PropertyTypeRendererFactory.get().from(propertyType);

            JComponent renderedComponent = renderer.render(module, objectProperty, nestedPropertyAccessor, propertiesPanel);

            renderer.addToParent(propertiesPanel, renderedComponent, objectProperty, propertiesPanel);
        });

        return propertiesPanel;
    }

    @NotNull
    private JComponent renderShareable(Module module, ComponentPropertyDescriptor descriptor, PropertyAccessor propertyAccessor, ContainerContext context) {
        ComponentDataHolder dataHolder = propertyAccessor.get();
        FlowSnapshot snapshot = propertyAccessor.getSnapshot();

        // We create the accessor for the config reference:
        // a shareable config object is referenced with a unique UUID
        PropertyAccessor refAccessor = PropertyAccessorFactory.get()
                .typeDescriptor(descriptor.getPropertyType())
                .propertyName(JsonParser.Component.ref())
                .snapshot(snapshot)
                .dataHolder(dataHolder)
                .build();

        return new ShareableConfigInputField(module, dataHolder, descriptor, refAccessor, context);
    }

    private void addToParentInline(@NotNull JComponent parent, @NotNull JComponent rendered, @NotNull ComponentPropertyDescriptor descriptor, @NotNull ContainerContext context) {
        // If the property type is a complex object (not shared), we wrap it in a
        // bordered box with title the name of the object property.
        TypeObjectDescriptor objectDescriptor = descriptor.getPropertyType();

        DisposablePanel wrappedRenderedComponent =
                createObjectTypeContainer(rendered, objectDescriptor, descriptor.getDisplayName());

        // If the property has any 'when' condition, we apply listener/s to make it
        // visible (or not) when the condition is met (or not).
        applyWhenVisibility(descriptor.getWhenDefinitions(), context, wrappedRenderedComponent);

        // Add the component to the parent container.
        FormBuilder.get()
                .addLastField(wrappedRenderedComponent, parent);
    }

    private DisposablePanel createObjectTypeContainer(
            @NotNull JComponent renderedComponent,
            @NotNull TypeObjectDescriptor descriptor,
            @NotNull String title) {
        return Collapsible.YES.equals(descriptor.getCollapsible()) ?
                new CollapsibleObjectTypeContainer(renderedComponent, title) :
                ContainerFactory.createObjectTypeContainer(renderedComponent, title);
    }
}

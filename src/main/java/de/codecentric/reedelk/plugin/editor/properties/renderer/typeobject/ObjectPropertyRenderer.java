package de.codecentric.reedelk.plugin.editor.properties.renderer.typeobject;

import de.codecentric.reedelk.plugin.editor.properties.commons.*;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContextDecorator;
import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import com.intellij.openapi.module.Module;
import com.intellij.util.ui.JBUI;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentDataHolder;
import de.codecentric.reedelk.module.descriptor.model.property.*;
import de.codecentric.reedelk.plugin.commons.TooltipContent;
import de.codecentric.reedelk.runtime.commons.JsonParser;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;


public class ObjectPropertyRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public RenderedComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor descriptor,
                             @NotNull PropertyAccessor accessor,
                             @NotNull ContainerContext context) {
        ObjectDescriptor objectDescriptor = descriptor.getType();
        JComponent component = Shared.YES.equals(objectDescriptor.getShared()) ?
                renderShareable(module, descriptor, accessor, context) :
                renderInline(module, accessor, descriptor, context);
        return RenderedComponent.create(component);
    }

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull RenderedComponent rendered,
                            @NotNull PropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {
        ObjectDescriptor objectDescriptor = descriptor.getType();
        if (Shared.NO.equals(objectDescriptor.getShared())) {
            addToParentInline(parent, rendered, descriptor, context);
        } else {
            super.addToParent(parent, rendered, descriptor, context);
        }
        // Add the component to the context
        context.addComponent(new JComponentHolder(rendered.component));
    }

    @NotNull
    private JComponent renderInline(Module module, PropertyAccessor propertyAccessor, PropertyDescriptor descriptor, @NotNull ContainerContext context) {
        ObjectDescriptor objectDescriptor = descriptor.getType();
        TooltipContent tooltipContent = TooltipContent.from(descriptor);
        if (Collapsible.YES.equals(objectDescriptor.getCollapsible())) {
            // Deferred rendering (only when it is un-collapsed)
            return new DisposableCollapsiblePane(
                    descriptor.getDisplayName(),
                    () -> {
                        JComponent content = renderObjectProperties(propertyAccessor, descriptor, module, context);
                        content.setBorder(JBUI.Borders.emptyLeft(20));
                        return content;
                    }, tooltipContent);
        } else {
            JComponent propertiesPanel = renderObjectProperties(propertyAccessor, descriptor, module, context);
            // If the property type is a complex object (not shared), we wrap it in a bordered box with title
            // the name of the object property.
            return ContainerFactory.createObjectTypeContainer(propertiesPanel, descriptor.getDisplayName(), tooltipContent);
        }
    }

    @NotNull
    private JComponent renderShareable(Module module, PropertyDescriptor descriptor, PropertyAccessor propertyAccessor, ContainerContext context) {
        ComponentDataHolder dataHolder = propertyAccessor.get();

        String propertyName = JsonParser.Component.ref();

        PropertyTypeDescriptor propertyType = descriptor.getType();

        // We create the accessor for the config reference:
        // a shareable config object is referenced with a unique UUID
        PropertyAccessor refAccessor = context.propertyAccessorOf(propertyName, propertyType, dataHolder);

        return new ShareableConfigInputField(module, dataHolder, descriptor, refAccessor, context);
    }

    private void addToParentInline(@NotNull JComponent parent,
                                   @NotNull RenderedComponent rendered,
                                   @NotNull PropertyDescriptor descriptor,
                                   @NotNull ContainerContext context) {
        // If the property has any 'when' condition, we apply listener/s to make it
        // visible (or not) when the condition is met (or not).
        WhenVisibilityApplier.on(descriptor, context, rendered);

        // Add the component to the parent container.
        FormBuilder.get().addFullWidthAndHeight(rendered.component, parent);
    }

    @NotNull
    private JComponent renderObjectProperties(PropertyAccessor propertyAccessor,
                                              PropertyDescriptor descriptor,
                                              Module module,
                                              @NotNull ContainerContext context) {
        // The accessor of type object returns a TypeObject map.
        ComponentDataHolder dataHolder = propertyAccessor.get();

        ObjectDescriptor objectDescriptor = descriptor.getType();

        List<PropertyDescriptor> objectProperties = objectDescriptor.getObjectProperties();

        // We are entering an object property: com.my.component#myObjectProperty1
        String propertyName = descriptor.getName();

        ContainerContext subPropertyContext = ContainerContextDecorator.decorateForProperty(propertyName, context);

        return new PropertiesPanelHolder(module, subPropertyContext, dataHolder, objectProperties);
    }
}

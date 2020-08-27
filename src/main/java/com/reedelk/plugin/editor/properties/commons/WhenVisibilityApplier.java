package com.reedelk.plugin.editor.properties.commons;

import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.module.descriptor.model.property.WhenDescriptor;
import com.reedelk.plugin.commons.AtLeastOneWhenConditionIsTrue;
import com.reedelk.plugin.commons.InitPropertyValue;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.editor.properties.renderer.PropertyTypeRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.groupingBy;

public class WhenVisibilityApplier {

    private WhenVisibilityApplier() {
    }

    /**
     * A component is visible when there are no when definitions, or if there is
     * any when definition defined for the property then at least one ot them
     * must be satisfied.
     */
    public static void on(@NotNull PropertyDescriptor propertyDescriptor,
                          @NotNull ContainerContext context,
                          @NotNull PropertyTypeRenderer.RenderedComponent... components) {

        List<WhenDescriptor> whens = propertyDescriptor.getWhens();

        // If there are no when definitions, then the component is visible (default visibility).
        if (whens.isEmpty()) return;

        // Apply visibility
        boolean isVisible = AtLeastOneWhenConditionIsTrue.of(whens, context::propertyValueFrom);
        setVisible(isVisible, components);

        // We need to group all the when definitions by property.
        // Apply on change listener
        whens.stream()
                .collect(groupingBy(WhenDescriptor::getPropertyName))
                .forEach((propertyName, whensForPropertyName) ->
                        context.subscribeOnPropertyChange(propertyName, newValue -> {
                            // Apply visibility to component
                            boolean shouldBeVisible =
                                    AtLeastOneWhenConditionIsTrue.of(whensForPropertyName, pName -> newValue);
                            setVisible(shouldBeVisible, components);

                            // When the component becomes visible we should re-set its initial
                            // value again so that it gets serialized in the JSON.
                            // This is useful when we select a drop down value and another
                            // drop down is dependent on the value of the selected drop down value.
                            // The second drop down needs to re-init its value if becomes visible.
                            if (shouldBeVisible) setInitValue(propertyDescriptor, components);
                        }));
    }

    /**
     * Sets the visibility of the provided components.
     * Note that we just swap the visibility if it has changed,
     * otherwise we don't change it and we save a call to revalidate.
     */
    private static void setVisible(boolean visible, PropertyTypeRenderer.RenderedComponent... components) {
        stream(components).forEach(renderedComponent -> {
            if (renderedComponent.component.isVisible() != visible) {
                renderedComponent.component.setVisible(visible);
                renderedComponent.component.revalidate();
            }
        });
    }

    /**
     * Sets the initial value for the components. The initial value is taken from the property descriptor.
     */
    public static void setInitValue(@NotNull PropertyDescriptor propertyDescriptor, @NotNull PropertyTypeRenderer.RenderedComponent[] components) {
        Object initPropertyValue = InitPropertyValue.of(propertyDescriptor);
        if (initPropertyValue != null) {
            stream(components).forEach(renderedComponent ->
                    renderedComponent.setInitValue(initPropertyValue));
        }
    }
}

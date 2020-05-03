package com.reedelk.plugin.editor.properties.commons;

import com.reedelk.module.descriptor.model.property.WhenDescriptor;
import com.reedelk.plugin.commons.AtLeastOneWhenConditionIsTrue;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
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
    public static void on(@NotNull List<WhenDescriptor> whens,
                                       @NotNull ContainerContext context,
                                       @NotNull JComponent... components) {
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
                            boolean shouldBeVisible =
                                    AtLeastOneWhenConditionIsTrue.of(whensForPropertyName, pName -> newValue);
                            setVisible(shouldBeVisible, components);
                        }));
    }

    /**
     * Sets the visibility of the provided components.
     * Note that we just swap the visibility if it has changed,
     * otherwise we don't change it and we save a call to revalidate.
     */
    private static void setVisible(boolean visible, JComponent... components) {
        stream(components).forEach(component -> {
            if (component.isVisible() != visible) {
                component.setVisible(visible);
                component.revalidate();
            }
        });
    }
}

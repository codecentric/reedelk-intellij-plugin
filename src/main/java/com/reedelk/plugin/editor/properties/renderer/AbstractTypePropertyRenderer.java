package com.reedelk.plugin.editor.properties.renderer;

import com.reedelk.plugin.commons.AtLeastOneWhenConditionIsTrue;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.WhenDefinition;
import com.reedelk.plugin.editor.properties.widget.ContainerContext;
import com.reedelk.plugin.editor.properties.widget.FormBuilder;
import com.reedelk.plugin.editor.properties.widget.JComponentHolder;
import com.reedelk.plugin.editor.properties.widget.PropertyTitleLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.groupingBy;

abstract class AbstractTypePropertyRenderer implements TypePropertyRenderer {

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull ComponentPropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {

        PropertyTitleLabel propertyTitleLabel = new PropertyTitleLabel(descriptor.getDisplayName());

        // Apply visibility conditions to the label and the rendered component
        applyWhenVisibility(descriptor.getWhenDefinitions(), context, rendered, propertyTitleLabel);

        // Add the component and its property title label to the
        // parent container.
        FormBuilder.get()
                .addLabel(propertyTitleLabel, parent)
                .addLastField(rendered, parent);

        // Add the component to the context.
        context.addComponent(new JComponentHolder(rendered));
    }

    /**
     * A component is visible when there are no when definitions, or if there is
     * any when definition defined for the property then at least one ot them
     * must be satisfied.
     */
    void applyWhenVisibility(List<WhenDefinition> whens, ContainerContext context, JComponent... components) {
        // If there are no when definitions, then the component is visible (default visibility).
        if (whens.isEmpty()) return;

        // Apply visibility
        boolean isVisible = AtLeastOneWhenConditionIsTrue.of(whens, context::propertyValueFrom);
        setVisible(isVisible, components);

        // We need to group all the when definitions by property.
        // Apply on change listener
        whens.stream()
                .collect(groupingBy(WhenDefinition::getPropertyName))
                .forEach((propertyName, whensForPropertyName) ->
                        context.subscribePropertyChange(propertyName, newValue -> {
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
    private void setVisible(boolean visible, JComponent... components) {
        stream(components).forEach(component -> {
            if (component.isVisible() != visible) {
                component.setVisible(visible);
                component.revalidate();
            }
        });
    }
}

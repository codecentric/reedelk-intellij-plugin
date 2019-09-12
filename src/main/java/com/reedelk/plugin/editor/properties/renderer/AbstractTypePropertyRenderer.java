package com.reedelk.plugin.editor.properties.renderer;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.WhenDefinition;
import com.reedelk.plugin.editor.properties.widget.ContainerContext;
import com.reedelk.plugin.editor.properties.widget.FormBuilder;
import com.reedelk.plugin.editor.properties.widget.JComponentHolder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

import static java.util.Arrays.stream;

abstract class AbstractTypePropertyRenderer implements TypePropertyRenderer {

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull ComponentPropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {

        JLabel propertyTitleLabel = new JLabel(descriptor.getDisplayName() + ":");

        // Apply visibility conditions to the label and the rendered component
        descriptor.getWhenDefinition().ifPresent(definition ->
                applyWhenVisibility(definition, context, rendered, propertyTitleLabel));

        // Add the component and its property title label to the
        // parent container.
        FormBuilder.get()
                .addLabel(propertyTitleLabel, parent)
                .addLastField(rendered, parent);

        // Add the component to the context.
        context.addComponent(new JComponentHolder(rendered));

    }

    void applyWhenVisibility(WhenDefinition when, ContainerContext context, JComponent... components) {
        String propertyName = when.getPropertyName();
        String propertyValue = when.getPropertyValue();
        Object actualPropertyValue = context.propertyValueFrom(propertyName);

        boolean conditionSatisfied = isConditionSatisfied(propertyValue, actualPropertyValue);
        setVisible(conditionSatisfied, components);

        context.subscribeOnPropertyChange(propertyName, value -> {
            boolean satisfied = isConditionSatisfied(propertyValue, value);
            setVisible(satisfied, components);
        });
    }

    private boolean isConditionSatisfied(String target, Object value) {
        return value != null && value.toString().equals(target);
    }

    private void setVisible(boolean visible, JComponent... components) {
        stream(components).forEach(component -> {
            component.setVisible(visible);
            component.repaint();
            component.revalidate();
        });
    }
}

package com.reedelk.plugin.editor.properties.renderer;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.WhenDefinition;
import com.reedelk.plugin.editor.properties.widget.ContainerContext;
import com.reedelk.plugin.editor.properties.widget.FormBuilder;
import com.reedelk.plugin.editor.properties.widget.JComponentHolder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

abstract class AbstractTypePropertyRenderer implements TypePropertyRenderer {

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull ComponentPropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {


        JLabel propertyTitleLabel = new JLabel(descriptor.getDisplayName() + ":");

        // Apply visibility conditions to the label and the rendered component
        applyWhenVisibilityConditions(descriptor.getWhenDefinitions(), context, rendered, propertyTitleLabel);

        // Add the component and its property title label to the
        // parent container.
        FormBuilder.get()
                .addLabel(propertyTitleLabel, parent)
                .addLastField(rendered, parent);

        // Add the component to the context.
        context.addComponent(new JComponentHolder(rendered));

    }

    // TODO: Is the following true? You might have multiple conditions????
    void applyWhenVisibilityConditions(List<WhenDefinition> whenDefinitions, ContainerContext context, JComponent... components) {
        whenDefinitions.forEach(whenDefinition -> {
            String propertyName = whenDefinition.getPropertyName();
            String propertyValue = whenDefinition.getPropertyValue();
            Object actualPropertyValue = context.propertyValueFrom(propertyName);

            setVisible(isConditionSatisfied(propertyValue, actualPropertyValue), components);

            context.subscribeOnPropertyChange(whenDefinition.getPropertyName(), value ->
                    setVisible(isConditionSatisfied(propertyValue, value), components));
        });
    }

    private boolean isConditionSatisfied(String target, Object value) {
        return value != null && value.toString().equals(target);
    }

    private void setVisible(boolean visible, JComponent... components) {
        Arrays.stream(components).forEach(component -> {
            component.setVisible(visible);
            component.repaint();
            component.revalidate();
        });
    }
}

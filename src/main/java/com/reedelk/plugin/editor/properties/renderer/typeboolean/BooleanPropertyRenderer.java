package com.reedelk.plugin.editor.properties.renderer.typeboolean;

import com.intellij.openapi.module.Module;
import com.intellij.util.ui.JBUI;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.editor.properties.commons.*;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.editor.properties.context.PropertyAccessor;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class BooleanPropertyRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor descriptor,
                             @NotNull PropertyAccessor accessor,
                             @NotNull ContainerContext context) {
        boolean selected = accessor.get() == null ? Boolean.FALSE : accessor.get();

        BooleanCheckbox checkbox = new BooleanCheckbox();
        checkbox.setSelected(selected);
        checkbox.addListener(accessor::set);

        PropertyTitleLabel propertyTitleLabel = new CheckboxPropertyTitle(descriptor);
        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 0, 0);

        JPanel container = new DisposablePanel();
        container.setLayout(flowLayout);
        container.add(checkbox);
        container.add(propertyTitleLabel);
        return container;
    }

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull PropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {

        // If the property has any 'when' condition, we apply listener/s to make it
        // visible (or not) when the condition is met (or not).
        WhenVisibilityApplier.on(descriptor.getWhens(), context, rendered);

        // Add the component to the parent container.
        FormBuilder.get().addFullWidthAndHeight(rendered, parent);

        // Add the component to the context.
        context.addComponent(new JComponentHolder(rendered));
    }

    static class CheckboxPropertyTitle extends PropertyTitleLabel {

        public CheckboxPropertyTitle(PropertyDescriptor propertyDescriptor) {
            super(propertyDescriptor);
            setBorder(JBUI.Borders.empty(0, 2));
        }

        @Override
        protected String createLabelText(String propertyDisplayName) {
            return propertyDisplayName + " "; // instead of ':' we use ' ' empty space.
        }
    }
}

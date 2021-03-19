package de.codecentric.reedelk.plugin.editor.properties.renderer.typeboolean;

import de.codecentric.reedelk.plugin.editor.properties.commons.*;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import com.intellij.openapi.module.Module;
import com.intellij.util.ui.JBUI;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class BooleanPropertyRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public RenderedComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor descriptor,
                             @NotNull PropertyAccessor accessor,
                             @NotNull ContainerContext context) {
        boolean selected = accessor.get() == null ? Boolean.FALSE : accessor.get();

        BooleanCheckbox checkbox = new BooleanCheckbox();
        checkbox.setSelected(selected);
        checkbox.addListener(accessor::set);

        PropertyTitleLabel propertyTitleLabel = new CheckboxPropertyTitle(descriptor);
        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 0, 4);

        JPanel container = new DisposablePanel();
        container.setLayout(flowLayout);
        container.add(checkbox);
        container.add(propertyTitleLabel);

        return RenderedComponent.create(container, value -> {
            boolean isSelected = value == null ? Boolean.FALSE : (boolean) value;
            checkbox.setSelected(isSelected);
        });
    }

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull RenderedComponent rendered,
                            @NotNull PropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {

        // If the property has any 'when' condition, we apply listener/s to make it
        // visible (or not) when the condition is met (or not).
        WhenVisibilityApplier.on(descriptor, context, rendered);

        // Add the component to the parent container.
        FormBuilder.get().addFullWidthAndHeight(rendered.component, parent);

        // Add the component to the context.
        context.addComponent(new JComponentHolder(rendered.component));
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

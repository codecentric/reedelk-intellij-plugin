package de.codecentric.reedelk.plugin.editor.properties.renderer;

import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public interface PropertyTypeRenderer {

    @NotNull
    RenderedComponent render(
            @NotNull Module module,
            @NotNull PropertyDescriptor propertyDescriptor,
            @NotNull PropertyAccessor propertyAccessor,
            @NotNull ContainerContext context);

    void addToParent(@NotNull JComponent parent,
                     @NotNull RenderedComponent rendered,
                     @NotNull PropertyDescriptor descriptor,
                     @NotNull ContainerContext context);

    class RenderedComponent {

        public final JComponent component;
        public final ValueSetter setter;

        private RenderedComponent(JComponent component, ValueSetter setter) {
            this.component = component;
            this.setter = setter;
        }

        public static RenderedComponent create(JComponent component, ValueSetter initValueSetter) {
            return new RenderedComponent(component, initValueSetter);
        }

        public static RenderedComponent create(JComponent component) {
            return new RenderedComponent(component, null);
        }

        public void setInitValue(Object initPropertyValue) {
            if (this.setter != null) {
                this.setter.set(initPropertyValue);
            }
        }
    }

    interface ValueSetter {
        void set(Object value);
    }
}

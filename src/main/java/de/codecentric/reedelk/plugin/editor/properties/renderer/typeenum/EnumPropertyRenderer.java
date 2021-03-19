package de.codecentric.reedelk.plugin.editor.properties.renderer.typeenum;

import de.codecentric.reedelk.plugin.editor.properties.commons.DisposablePanel;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.module.descriptor.model.property.EnumDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.WEST;
import static java.util.Optional.ofNullable;

public class EnumPropertyRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public RenderedComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        EnumDescriptor propertyType = propertyDescriptor.getType();

        EnumDropDown dropDown = new EnumDropDown(propertyType.getNameAndDisplayNameMap());

        // We set the default value in the dropdown if present.
        ofNullable(propertyDescriptor.getDefaultValue()).ifPresent(dropDown::setValue);

        // We only add it if it is not null.
        ofNullable(propertyAccessor.get())
                .ifPresent(value -> dropDown.setValue(propertyAccessor.get()));

        dropDown.addListener(propertyAccessor::set);

        JPanel dropDownContainer = new DisposablePanel(new BorderLayout());
        dropDownContainer.add(dropDown, WEST);
        return RenderedComponent.create(dropDownContainer, dropDown::setValue);
    }
}

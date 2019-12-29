package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.reedelk.component.descriptor.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.renderer.commons.InputField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;
import static javax.swing.Box.createHorizontalBox;

public abstract class AbstractNumericPropertyRenderer<T> extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull ComponentPropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        InputField<T> inputField = getInputField(propertyDescriptor.getHintValue());
        inputField.setValue(propertyAccessor.get());
        inputField.addListener(propertyAccessor::set);

        JPanel inputFieldContainer = new DisposablePanel(new BorderLayout());
        inputFieldContainer.add(inputField, WEST);
        inputFieldContainer.add(createHorizontalBox(), CENTER);
        return inputFieldContainer;
    }

    protected abstract InputField<T> getInputField(String hint);

}
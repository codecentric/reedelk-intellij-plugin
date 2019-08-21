package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.widget.DisposablePanel;
import com.reedelk.plugin.editor.properties.widget.input.InputField;
import com.reedelk.plugin.editor.properties.widget.input.script.ContainerContext;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.WEST;
import static javax.swing.Box.createHorizontalBox;

public abstract class NumericPropertyRenderer<T> implements TypePropertyRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull ComponentPropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        InputField<T> inputField = getInputField();
        inputField.setValue(propertyAccessor.get());
        inputField.addListener(propertyAccessor::set);

        JPanel inputFieldContainer = new DisposablePanel(new BorderLayout());
        inputFieldContainer.add(inputField, WEST);
        inputFieldContainer.add(createHorizontalBox(), CENTER);
        return inputFieldContainer;
    }

    protected abstract InputField<T> getInputField();
}

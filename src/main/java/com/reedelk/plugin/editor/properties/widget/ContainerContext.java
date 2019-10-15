package com.reedelk.plugin.editor.properties.widget;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.widget.input.InputChangeListener;

import javax.swing.*;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public interface ContainerContext {

    <T> T propertyValueFrom(String propertyName);

    void subscribePropertyChange(String propertyName, InputChangeListener inputChangeListener);

    Optional<ComponentPropertyDescriptor> getPropertyDescriptor(Predicate<ComponentPropertyDescriptor> filter);

    void addComponent(JComponentHolder componentHolder);

    Optional<JComponent> getComponentMatchingMetadata(BiPredicate<String, String> keyValuePredicate);

    <T> void notifyPropertyChanged(String propertyName, T object);

}

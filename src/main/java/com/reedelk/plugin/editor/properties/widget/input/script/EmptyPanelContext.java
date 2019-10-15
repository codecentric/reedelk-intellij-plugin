package com.reedelk.plugin.editor.properties.widget.input.script;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.widget.ContainerContext;
import com.reedelk.plugin.editor.properties.widget.JComponentHolder;
import com.reedelk.plugin.editor.properties.widget.input.InputChangeListener;

import javax.swing.*;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

class EmptyPanelContext implements ContainerContext {

    @Override
    public void subscribePropertyChange(String propertyName, InputChangeListener inputChangeListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T propertyValueFrom(String propertyName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<ComponentPropertyDescriptor> getPropertyDescriptor(Predicate<ComponentPropertyDescriptor> filter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addComponent(JComponentHolder componentHolder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<JComponent> getComponentMatchingMetadata(BiPredicate<String, String> keyValuePredicate) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void notifyPropertyChanged(String propertyName, T object) {
        throw new UnsupportedOperationException();
    }
}

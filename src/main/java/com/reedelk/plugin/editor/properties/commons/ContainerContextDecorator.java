package com.reedelk.plugin.editor.properties.commons;

import javax.swing.*;
import java.util.Optional;
import java.util.function.BiPredicate;

public class ContainerContextDecorator implements ContainerContext {

    private final String propertyName;
    private final ContainerContext delegate;

    private ContainerContextDecorator(String propertyName, ContainerContext delegate) {
        this.delegate = delegate;
        this.propertyName = propertyName;
    }

    public static ContainerContext decorateForProperty(String currentProperty, ContainerContext delegate) {
        return new ContainerContextDecorator(currentProperty, delegate);
    }

    @Override
    public String componentPropertyPath() {
        return delegate.componentPropertyPath() + "#" + propertyName;
    }

    @Override
    public <T> T propertyValueFrom(String propertyName) {
        return delegate.propertyValueFrom(propertyName);
    }

    @Override
    public <T> void notifyPropertyChange(String propertyName, T object) {
        delegate.notifyPropertyChange(propertyName, object);
    }

    @Override
    public Optional<JComponent> findComponentMatchingMetadata(BiPredicate<String, String> keyValuePredicate) {
        return delegate.findComponentMatchingMetadata(keyValuePredicate);
    }

    @Override
    public void subscribeOnPropertyChange(String propertyName, InputChangeListener inputChangeListener) {
        delegate.subscribeOnPropertyChange(propertyName, inputChangeListener);
    }

    @Override
    public void addComponent(JComponentHolder componentHolder) {
        delegate.addComponent(componentHolder);
    }
}

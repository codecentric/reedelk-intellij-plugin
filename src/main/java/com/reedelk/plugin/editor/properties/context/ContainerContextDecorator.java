package com.reedelk.plugin.editor.properties.context;

import com.reedelk.module.descriptor.model.component.ComponentDataHolder;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.editor.properties.commons.InputChangeListener;
import com.reedelk.plugin.editor.properties.commons.JComponentHolder;
import com.reedelk.plugin.graph.FlowSnapshot;

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
        return ComponentPropertyPath.join(delegate.componentPropertyPath(), propertyName);
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
    public PropertyAccessor getPropertyAccessor(PropertyDescriptor propertyDescriptor, FlowSnapshot snapshot, ComponentDataHolder dataHolder) {
        return delegate.getPropertyAccessor(propertyDescriptor, snapshot, dataHolder);
    }

    @Override
    public void addComponent(JComponentHolder componentHolder) {
        delegate.addComponent(componentHolder);
    }
}

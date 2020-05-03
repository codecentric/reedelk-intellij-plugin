package com.reedelk.plugin.editor.properties.context;

import com.reedelk.module.descriptor.model.component.ComponentDataHolder;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.editor.properties.commons.InputChangeListener;
import com.reedelk.plugin.editor.properties.commons.JComponentHolder;
import com.reedelk.plugin.graph.FlowSnapshot;

import javax.swing.*;
import java.util.Optional;
import java.util.function.BiPredicate;

public interface ContainerContext {

    String componentPropertyPath();

    <T> T propertyValueFrom(String propertyName);

    void addComponent(JComponentHolder componentHolder);

    Optional<JComponent> findComponentMatchingMetadata(BiPredicate<String, String> keyValuePredicate);

    <T> void notifyPropertyChange(String propertyName, T object);

    void subscribeOnPropertyChange(String propertyName, InputChangeListener inputChangeListener);

    PropertyAccessor getPropertyAccessor(PropertyDescriptor propertyDescriptor, FlowSnapshot snapshot, ComponentDataHolder dataHolder);

}

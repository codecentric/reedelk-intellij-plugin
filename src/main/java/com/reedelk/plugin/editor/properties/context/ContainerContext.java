package com.reedelk.plugin.editor.properties.context;

import com.intellij.openapi.Disposable;
import com.reedelk.module.descriptor.model.component.ComponentDataHolder;
import com.reedelk.module.descriptor.model.property.PropertyTypeDescriptor;
import com.reedelk.plugin.editor.properties.commons.InputChangeListener;
import com.reedelk.plugin.editor.properties.commons.JComponentHolder;
import com.reedelk.plugin.graph.FlowSnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Optional;
import java.util.function.BiPredicate;

public interface ContainerContext extends Disposable {

    String componentPropertyPath();

    <T> T propertyValueFrom(String propertyName);

    void addComponent(JComponentHolder componentHolder);

    Optional<JComponent> findComponentMatchingMetadata(BiPredicate<String, String> keyValuePredicate);

    <T> void notifyPropertyChange(String propertyName, T object);

    void subscribeOnPropertyChange(String propertyName, InputChangeListener inputChangeListener);


    PropertyAccessor propertyAccessorOf(String propertyName,
                                        @NotNull PropertyTypeDescriptor propertyTypeDescriptor,
                                        @Nullable FlowSnapshot snapshot,
                                        @NotNull ComponentDataHolder dataHolder);
}

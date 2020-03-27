package com.reedelk.plugin.editor.properties.commons;

import javax.swing.*;
import java.util.Optional;
import java.util.function.BiPredicate;

public interface ContainerContext {

    String componentFullyQualifiedName();

    <T> T propertyValueFrom(String propertyName);

    <T> void notifyPropertyChanged(String propertyName, T object);

    Optional<JComponent> getComponentMatchingMetadata(BiPredicate<String, String> keyValuePredicate);

    void subscribePropertyChange(String propertyName, InputChangeListener inputChangeListener);

    void addComponent(JComponentHolder componentHolder);

}

package com.reedelk.plugin.editor.properties.commons;

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

}

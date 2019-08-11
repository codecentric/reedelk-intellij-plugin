package com.reedelk.plugin.editor.properties.widget.input.script;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.widget.input.InputChangeListener;

import java.util.Optional;
import java.util.function.Predicate;

public interface PropertyPanelContext {

    void subscribe(String propertyName, InputChangeListener<?> inputChangeListener);

    <T> T getPropertyValue(String propertyName);

    Optional<ComponentPropertyDescriptor> getDescriptorMatching(Predicate<ComponentPropertyDescriptor> filter);

}

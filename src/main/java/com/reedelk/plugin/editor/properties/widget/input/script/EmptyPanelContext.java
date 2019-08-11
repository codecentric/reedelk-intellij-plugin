package com.reedelk.plugin.editor.properties.widget.input.script;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.widget.input.InputChangeListener;

import java.util.Optional;
import java.util.function.Predicate;

class EmptyPanelContext implements PropertyPanelContext {

    @Override
    public void subscribe(String propertyName, InputChangeListener<?> inputChangeListener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getPropertyValue(String propertyName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<ComponentPropertyDescriptor> getDescriptorMatching(Predicate<ComponentPropertyDescriptor> filter) {
        throw new UnsupportedOperationException();
    }
}

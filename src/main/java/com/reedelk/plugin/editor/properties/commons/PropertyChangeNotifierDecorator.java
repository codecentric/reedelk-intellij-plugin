package com.reedelk.plugin.editor.properties.commons;

import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.graph.FlowSnapshot;

/**
 * Decorator which notifies all the listeners of a specific property
 * change that a property has been changed.
 */
class PropertyChangeNotifierDecorator implements PropertyAccessor {

    private final PropertyAccessor wrapped;
    private final ContainerContext context;

    PropertyChangeNotifierDecorator(ContainerContext context, PropertyAccessor wrapped) {
        this.wrapped = wrapped;
        this.context = context;
    }

    @Override
    public FlowSnapshot getSnapshot() {
        return wrapped.getSnapshot();
    }

    @Override
    public <T> void set(T object) {
        wrapped.set(object);
        String propertyName = wrapped.getProperty();
        context.notifyPropertyChange(propertyName, object);
    }

    @Override
    public <T> T get() {
        return wrapped.get();
    }

    @Override
    public String getProperty() {
        return wrapped.getProperty();
    }
}

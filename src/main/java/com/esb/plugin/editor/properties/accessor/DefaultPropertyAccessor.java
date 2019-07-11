package com.esb.plugin.editor.properties.accessor;

import com.esb.plugin.component.domain.ComponentDataHolder;
import com.esb.plugin.graph.FlowSnapshot;

public class DefaultPropertyAccessor implements PropertyAccessor {

    private final ComponentDataHolder data;
    private final FlowSnapshot snapshot;
    private final String propertyName;

    /**
     * Constructor used by Accessors who want to immediately notify the graph that a property has changed.
     *
     * @param propertyName the property name this accessor is referring to
     * @param dataHolder   the container containing the value of the property named 'propertyName'
     * @param snapshot     the Flow snapshot to notify about some data being changed
     */
    public DefaultPropertyAccessor(String propertyName, ComponentDataHolder dataHolder, FlowSnapshot snapshot) {
        this.propertyName = propertyName;
        this.data = dataHolder;
        this.snapshot = snapshot;
    }

    @Override
    public void set(Object object) {
        data.set(propertyName, object);
        if (snapshot != null) {
            snapshot.onDataChange();
        }
    }

    @Override
    public <T> T get() {
        return data.get(propertyName);
    }

    @Override
    public FlowSnapshot getSnapshot() {
        return snapshot;
    }
}

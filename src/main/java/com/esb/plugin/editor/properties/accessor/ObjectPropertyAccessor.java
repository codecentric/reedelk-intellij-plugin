package com.esb.plugin.editor.properties.accessor;

import com.esb.plugin.component.domain.ComponentDataHolder;
import com.esb.plugin.graph.FlowSnapshot;

public class ObjectPropertyAccessor implements PropertyAccessor {

    private final ComponentDataHolder data;
    private final FlowSnapshot snapshot;
    private final String propertyName;

    public ObjectPropertyAccessor(String propertyName, ComponentDataHolder dataHolder) {
        this(propertyName, dataHolder, null);
    }

    public ObjectPropertyAccessor(String propertyName, ComponentDataHolder dataHolder, FlowSnapshot snapshot) {
        this.propertyName = propertyName;
        this.snapshot = snapshot;
        this.data = dataHolder;
    }

    @Override
    public void set(Object object) {
        data.set(propertyName, object);
        if (snapshot != null) {
            snapshot.onDataChange();
        }
    }

    @Override
    public Object get() {
        return data.get(propertyName);
    }

    @Override
    public FlowSnapshot getSnapshot() {
        return snapshot;
    }
}

package com.reedelk.plugin.action.importopenapi;

import com.reedelk.plugin.editor.properties.context.PropertyAccessor;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.runtime.api.commons.StringUtils;

public class PropertyAccessorInMemory implements PropertyAccessor {

    private Object value = StringUtils.EMPTY;

    @Override
    public FlowSnapshot getSnapshot() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void set(T object) {
        this.value = object;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get() {
        return (T) this.value;
    }

    @Override
    public String getProperty() {
        throw new UnsupportedOperationException();
    }
}

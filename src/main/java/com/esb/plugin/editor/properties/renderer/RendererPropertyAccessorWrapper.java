package com.esb.plugin.editor.properties.renderer;

import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.graph.FlowSnapshot;

public class RendererPropertyAccessorWrapper implements PropertyAccessor {

    private final PropertyAccessor wrapped;
    private final DefaultPropertiesPanel propertiesPanel;

    public RendererPropertyAccessorWrapper(PropertyAccessor wrapped, DefaultPropertiesPanel propertiesPanel) {
        this.wrapped = wrapped;
        this.propertiesPanel = propertiesPanel;
    }

    @Override
    public FlowSnapshot getSnapshot() {
        return this.wrapped.getSnapshot();
    }

    @Override
    public <T> void set(T object) {
        this.wrapped.set(object);
        this.propertiesPanel.notify(wrapped.getProperty(), object);
    }

    @Override
    public <T> T get() {
        return this.wrapped.get();
    }

    @Override
    public String getProperty() {
        return this.wrapped.getProperty();
    }
}

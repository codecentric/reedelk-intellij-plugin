package de.codecentric.reedelk.plugin.editor.properties.context;

import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentDataHolder;

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

    /**
     * Constructor used by Accessors who don't want to notify the graph that something has changed.
     * This is being used by Configuration accessor. Configuration it is saved only when the user
     * confirms the operation from the dialog. Configuration is stored in a different file from flow files.
     *
     * @param propertyName the property name this accessor is referring to
     * @param dataHolder   the container containing the value of the property named 'propertyName'
     */
    public DefaultPropertyAccessor(String propertyName, ComponentDataHolder dataHolder) {
        this(propertyName, dataHolder, null);
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

    @Override
    public String getProperty() {
        return propertyName;
    }
}

package com.reedelk.plugin.editor.properties.widget;

import com.reedelk.plugin.component.domain.ComponentDataHolder;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.reedelk.plugin.editor.properties.widget.input.InputChangeListener;
import com.reedelk.plugin.editor.properties.widget.input.script.PropertyPanelContext;
import com.reedelk.plugin.graph.FlowSnapshot;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;

public class PropertiesPanelHolder extends DisposablePanel implements PropertyPanelContext {

    private final Map<String, List<InputChangeListener>> propertyChangeListeners = new HashMap<>();
    private final Map<String, PropertyAccessor> propertyAccessors = new HashMap<>();

    private final List<ComponentPropertyDescriptor> descriptors;
    private final ComponentDataHolder dataHolder;
    private final FlowSnapshot snapshot;


    public PropertiesPanelHolder(ComponentDataHolder dataHolder, FlowSnapshot snapshot) {
        this(dataHolder, Collections.emptyList(), snapshot);
    }

    /**
     * Constructor used by a configuration panel Dialog. The configuration panel Dialog does not
     * immediately change the values on the Graph snapshot since it writes the values in a a config file.
     */
    public PropertiesPanelHolder(ComponentDataHolder dataHolder, List<ComponentPropertyDescriptor> descriptors) {
        this(dataHolder, descriptors, null);
    }

    public PropertiesPanelHolder(ComponentDataHolder dataHolder, List<ComponentPropertyDescriptor> descriptors, FlowSnapshot snapshot) {
        super(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        this.snapshot = snapshot;
        this.dataHolder = dataHolder;
        this.descriptors = descriptors;

        initAccessors();
    }

    @Override
    public void subscribe(String propertyName, InputChangeListener<?> inputChangeListener) {
        List<InputChangeListener> changeListenersForProperty =
                propertyChangeListeners.getOrDefault(propertyName, new ArrayList<>());
        changeListenersForProperty.add(inputChangeListener);
        propertyChangeListeners.put(propertyName, changeListenersForProperty);
    }

    @Override
    public <T> T getPropertyValue(String propertyName) {
        return propertyAccessors.get(propertyName).get();
    }

    @Override
    public Optional<ComponentPropertyDescriptor> getDescriptorMatching(Predicate<ComponentPropertyDescriptor> filter) {
        return descriptors.stream().filter(filter).findFirst();
    }

    public PropertyAccessor getAccessor(String propertyName) {
        return this.propertyAccessors.get(propertyName);
    }

    private void initAccessors() {
        // We decorate each accessor with a property change decorator, which
        // notifies all the subscribers wishing to listen for a property change event
        // to be notified. This is needed for instance to re-compute suggestions when
        // a new JSON schema file is selected from a file chooser input field.
        descriptors.forEach(propertyDescriptor -> {
            String propertyName = propertyDescriptor.getPropertyName();
            TypeDescriptor propertyType = propertyDescriptor.getPropertyType();
            PropertyAccessor propertyAccessor = getAccessor(propertyName, propertyType, dataHolder);
            PropertyChangeNotifierDecorator propertyAccessorWrapper = new PropertyChangeNotifierDecorator(propertyAccessor);
            propertyAccessors.put(propertyName, propertyAccessorWrapper);
        });
    }

    protected PropertyAccessor getAccessor(String propertyName, TypeDescriptor propertyType, ComponentDataHolder dataHolder) {
        return PropertyAccessorFactory.get()
                .typeDescriptor(propertyType)
                .propertyName(propertyName)
                .dataHolder(dataHolder)
                .snapshot(snapshot)
                .build();
    }

    /**
     * Decorator which notifies all the listeners of a specific property
     * change that a property has been changed.
     */
    class PropertyChangeNotifierDecorator implements PropertyAccessor {

        private final PropertyAccessor wrapped;

        PropertyChangeNotifierDecorator(PropertyAccessor wrapped) {
            this.wrapped = wrapped;
        }

        @Override
        public FlowSnapshot getSnapshot() {
            return wrapped.getSnapshot();
        }

        @Override
        public <T> void set(T object) {
            wrapped.set(object);
            String propertyName = wrapped.getProperty();
            if (propertyChangeListeners.containsKey(propertyName)) {
                propertyChangeListeners.get(propertyName)
                        .forEach(inputChangeListener -> inputChangeListener.onChange(object));
            }
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
}

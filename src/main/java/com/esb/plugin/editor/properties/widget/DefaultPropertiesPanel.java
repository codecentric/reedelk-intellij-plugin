package com.esb.plugin.editor.properties.widget;

import com.esb.plugin.component.domain.ComponentData;
import com.esb.plugin.component.domain.ComponentDataHolder;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.esb.plugin.editor.properties.widget.input.InputChangeListener;
import com.esb.plugin.graph.FlowSnapshot;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;

public class DefaultPropertiesPanel extends JBPanel implements PropertyPanelContext {

    private final Map<String, List<InputChangeListener>> propertyChangeListeners = new HashMap<>();
    private final Map<String, PropertyAccessor> propertyAccessors = new HashMap<>();

    private final List<ComponentPropertyDescriptor> descriptors;
    private final ComponentDataHolder componentData;
    private final FlowSnapshot snapshot;


    public DefaultPropertiesPanel(ComponentData componentData, FlowSnapshot snapshot) {
        this(componentData, Collections.emptyList(), snapshot);
    }

    public DefaultPropertiesPanel(ComponentDataHolder componentData, List<ComponentPropertyDescriptor> descriptors) {
        this(componentData, descriptors, null);
    }

    public DefaultPropertiesPanel(ComponentDataHolder componentData, List<ComponentPropertyDescriptor> descriptors, FlowSnapshot snapshot) {
        super(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        this.snapshot = snapshot;
        this.descriptors = descriptors;
        this.componentData = componentData;
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
    public <T> void notify(String propertyName, T newValue) {
        if (propertyChangeListeners.containsKey(propertyName)) {
            propertyChangeListeners.get(propertyName).forEach(inputChangeListener ->
                    inputChangeListener.onChange(newValue));
        }
    }

    @Override
    public <T> T getPropertyValue(String propertyName) {
        return propertyAccessors.get(propertyName).get();
    }

    @Override
    public Optional<ComponentPropertyDescriptor> getDescriptorMatching(Predicate<ComponentPropertyDescriptor> filter) {
        return descriptors.stream().filter(filter).findFirst();
    }


    private void initAccessors() {
        descriptors.forEach(propertyDescriptor -> {
            String propertyName = propertyDescriptor.getPropertyName();
            TypeDescriptor propertyType = propertyDescriptor.getPropertyType();
            PropertyAccessor propertyAccessor = getAccessor(propertyName, propertyType, componentData);
            RendererPropertyAccessorWrapper propertyAccessorWrapper = new RendererPropertyAccessorWrapper(propertyAccessor, this);
            propertyAccessors.put(propertyName, propertyAccessorWrapper);
        });
    }

    public PropertyAccessor getAccessor(String propertyName) {
        return this.propertyAccessors.get(propertyName);
    }

    protected PropertyAccessor getAccessor(String propertyName, TypeDescriptor propertyType, ComponentDataHolder dataHolder) {
        return PropertyAccessorFactory.get()
                .typeDescriptor(propertyType)
                .propertyName(propertyName)
                .dataHolder(dataHolder)
                .snapshot(snapshot)
                .build();
    }

    class RendererPropertyAccessorWrapper implements PropertyAccessor {

        private final PropertyAccessor wrapped;
        private final DefaultPropertiesPanel propertiesPanel;

        RendererPropertyAccessorWrapper(PropertyAccessor wrapped, DefaultPropertiesPanel propertiesPanel) {
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
}

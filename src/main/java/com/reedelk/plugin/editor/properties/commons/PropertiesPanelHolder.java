package com.reedelk.plugin.editor.properties.commons;

import com.reedelk.plugin.component.domain.ComponentDataHolder;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.reedelk.plugin.editor.properties.renderer.commons.InputChangeListener;
import com.reedelk.plugin.graph.FlowSnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.BiPredicate;

import static com.intellij.util.ui.JBUI.Borders;

public class PropertiesPanelHolder extends DisposablePanel implements ContainerContext {

    private final String componentFullyQualifiedName;
    private final transient FlowSnapshot snapshot;
    private final transient ComponentDataHolder dataHolder;

    private final transient List<JComponentHolder> componentHolders = new ArrayList<>();
    private final transient List<ComponentPropertyDescriptor> descriptors = new ArrayList<>();

    private final transient Map<String, PropertyAccessor> propertyAccessors = new HashMap<>();
    private final transient Map<String, List<InputChangeListener>> propertyChangeListeners = new HashMap<>();

    public PropertiesPanelHolder(@NotNull String componentFullyQualifiedName,
                                 @NotNull ComponentDataHolder dataHolder,
                                 @NotNull List<ComponentPropertyDescriptor> descriptors,
                                 @Nullable FlowSnapshot snapshot) {
        this.snapshot = snapshot;
        this.dataHolder = dataHolder;
        this.componentFullyQualifiedName = componentFullyQualifiedName;
        this.descriptors.addAll(descriptors);

        setLayout(new GridBagLayout());
        setBorder(Borders.emptyRight(10));
        initAccessors();
    }

    /**
     * Constructor used by a configuration panel Dialog. The configuration panel Dialog does not
     * immediately change the values on the Graph snapshot since it writes the values in a a config file.
     */
    public PropertiesPanelHolder(@NotNull String componentFullyQualifiedName, ComponentDataHolder dataHolder, List<ComponentPropertyDescriptor> descriptors) {
        this(componentFullyQualifiedName, dataHolder, descriptors, null);
    }

    @Override
    public void subscribePropertyChange(String propertyName, InputChangeListener inputChangeListener) {
        List<InputChangeListener> changeListenersForProperty =
                propertyChangeListeners.getOrDefault(propertyName, new ArrayList<>());
        changeListenersForProperty.add(inputChangeListener);
        propertyChangeListeners.put(propertyName, changeListenersForProperty);
    }

    @Override
    public <T> T propertyValueFrom(String propertyName) {
        // When an Enum does not have a default value, the property accessor
        // might be null. In this case its value would be null.
        PropertyAccessor propertyAccessor = propertyAccessors.get(propertyName);
        return propertyAccessor != null ? propertyAccessor.get() : null;
    }

    @Override
    public void addComponent(JComponentHolder componentHolder) {
        componentHolders.add(componentHolder);
    }

    @Override
    public Optional<JComponent> getComponentMatchingMetadata(BiPredicate<String, String> metadataPredicate) {
        for (JComponentHolder holder : componentHolders) {
            if (holder.matches(metadataPredicate)) {
                return Optional.of(holder.getComponent());
            }
        }
        return Optional.empty();
    }

    @Override
    public <T> void notifyPropertyChanged(String propertyName, T object) {
        if (propertyChangeListeners.containsKey(propertyName)) {
            propertyChangeListeners.get(propertyName)
                    .forEach(inputChangeListener -> inputChangeListener.onChange(object));
        }
    }

    @Override
    public String componentFullyQualifiedName() {
        return componentFullyQualifiedName;
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
            PropertyAccessor propertyAccessorWrapper = new PropertyChangeNotifierDecorator(this, propertyAccessor);
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
}

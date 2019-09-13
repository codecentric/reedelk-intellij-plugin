package com.reedelk.plugin.editor.properties.widget;

import com.reedelk.plugin.component.domain.ComponentDataHolder;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.reedelk.plugin.editor.properties.widget.input.InputChangeListener;
import com.reedelk.plugin.graph.FlowSnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import static com.intellij.util.ui.JBUI.Borders;

public class PropertiesPanelHolder extends DisposablePanel implements ContainerContext {

    private final FlowSnapshot snapshot;
    private final ComponentDataHolder dataHolder;

    private final List<JComponentHolder> componentHolders = new ArrayList<>();
    private final List<ComponentPropertyDescriptor> descriptors = new ArrayList<>();

    private final Map<String, PropertyAccessor> propertyAccessors = new HashMap<>();
    private final Map<String, List<InputChangeListener>> propertyChangeListeners = new HashMap<>();

    public PropertiesPanelHolder(@NotNull ComponentDataHolder dataHolder,
                                 @NotNull List<ComponentPropertyDescriptor> descriptors,
                                 @Nullable FlowSnapshot snapshot) {
        this.snapshot = snapshot;
        this.dataHolder = dataHolder;
        this.descriptors.addAll(descriptors);

        setLayout(new GridBagLayout());
        setBorder(Borders.emptyRight(10));
        initAccessors();
    }

    /**
     * Constructor used by a configuration panel Dialog. The configuration panel Dialog does not
     * immediately change the values on the Graph snapshot since it writes the values in a a config file.
     */
    public PropertiesPanelHolder(ComponentDataHolder dataHolder, List<ComponentPropertyDescriptor> descriptors) {
        this(dataHolder, descriptors, null);
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
        return propertyAccessors.get(propertyName).get();
    }

    @Override
    public Optional<ComponentPropertyDescriptor> getPropertyDescriptor(Predicate<ComponentPropertyDescriptor> filter) {
        return descriptors.stream().filter(filter).findFirst();
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

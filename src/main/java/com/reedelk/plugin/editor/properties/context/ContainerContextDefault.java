package com.reedelk.plugin.editor.properties.context;

import com.reedelk.module.descriptor.model.component.ComponentDataHolder;
import com.reedelk.module.descriptor.model.property.PropertyTypeDescriptor;
import com.reedelk.plugin.editor.properties.commons.InputChangeListener;
import com.reedelk.plugin.editor.properties.commons.JComponentHolder;
import com.reedelk.plugin.graph.FlowSnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;
import java.util.function.BiPredicate;

public class ContainerContextDefault implements ContainerContext {

    private final String componentPropertyPath;
    private final transient List<JComponentHolder> componentHolders = new ArrayList<>();
    private final transient Map<String, PropertyAccessor> changeAwarePropertyAccessor = new HashMap<>();
    private final transient Map<String, List<InputChangeListener>> propertyChangeListeners = new HashMap<>();

    public ContainerContextDefault(String componentPropertyPath) {
        this.componentPropertyPath = componentPropertyPath;
    }

    @Override
    public String componentPropertyPath() {
        return componentPropertyPath;
    }

    @Override
    public <T> T propertyValueFrom(String propertyName) {
        // When an Enum does not have a default value, the property accessor
        // might be null. In this case its value would be null.
        String propertyPath = getPropertyPath(propertyName);
        PropertyAccessor propertyAccessor = changeAwarePropertyAccessor.get(propertyPath);
        return propertyAccessor != null ? propertyAccessor.get() : null;
    }

    @Override
    public void addComponent(JComponentHolder componentHolder) {
        componentHolders.add(componentHolder);
    }

    @Override
    public Optional<JComponent> findComponentMatchingMetadata(BiPredicate<String, String> metadataPredicate) {
        return componentHolders.stream()
                .filter(holder -> holder.matches(metadataPredicate))
                .map(JComponentHolder::getComponent)
                .findFirst();
    }

    @Override
    public <T> void notifyPropertyChange(String propertyName, T object) {
        String propertyPath = getPropertyPath(propertyName);
        if (propertyChangeListeners.containsKey(propertyPath)) {
            propertyChangeListeners.get(propertyPath)
                    .forEach(inputChangeListener -> inputChangeListener.onChange(object));
        }
    }

    @Override
    public void subscribeOnPropertyChange(String propertyName, InputChangeListener inputChangeListener) {
        String propertyPath = getPropertyPath(propertyName);
        List<InputChangeListener> changeListenersForProperty =
                propertyChangeListeners.getOrDefault(propertyPath, new ArrayList<>());
        changeListenersForProperty.add(inputChangeListener);
        propertyChangeListeners.put(propertyPath, changeListenersForProperty);
    }

    // We decorate each accessor with a property change decorator, which
    // notifies all the subscribers wishing to listen for a property change event
    // to be notified. This is needed for instance show/hide other properties using @When annotation.
    @Override
    public PropertyAccessor propertyAccessorOf(String propertyName,
                                               @NotNull PropertyTypeDescriptor propertyType,
                                               @Nullable FlowSnapshot snapshot,
                                               @NotNull ComponentDataHolder dataHolder) {

        // Original property accessor.
        PropertyAccessor propertyAccessor = PropertyAccessorFactory.get()
                .typeDescriptor(propertyType)
                .propertyName(propertyName)
                .dataHolder(dataHolder)
                .snapshot(snapshot)
                .build();

        // Change aware accessor.
        PropertyAccessor propertyAccessorWrapper = new PropertyChangeNotifierDecorator(this, propertyAccessor);

        String propertyPath = getPropertyPath(propertyName);

        changeAwarePropertyAccessor.put(propertyPath, propertyAccessorWrapper);

        return propertyAccessorWrapper;
    }

    @NotNull
    protected String getPropertyPath(String propertyName) {
        // Property path is: com.my.component.fully.qualified.name#property1#subproperty1#subsubproperty1
        //  or: com.my.component.fully.qualified.name#property2#subproperty1
        return ComponentPropertyPath.join(componentPropertyPath(), propertyName);
    }
}

package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentDataHolder;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyTypeDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.reedelk.plugin.editor.properties.renderer.PropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.PropertyTypeRendererFactory;
import com.reedelk.plugin.graph.FlowSnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.BiPredicate;

import static com.intellij.util.ui.JBUI.Borders.empty;
import static java.util.Collections.emptyList;

public class PropertiesPanelHolder extends DisposablePanel implements ContainerContext {

    private static final Border PANEL_BORDERS = empty(8, 0, 0, 10);

    private final String componentPropertyPath;

    private final transient Module module;
    private final transient FlowSnapshot snapshot;
    private final transient ComponentDataHolder dataHolder;
    private final transient List<PropertyDescriptor> descriptors = new ArrayList<>();
    private final transient List<JComponentHolder> componentHolders = new ArrayList<>();
    private final transient Map<String, PropertyAccessor> changeAwarePropertyAccessor = new HashMap<>();
    private final transient Map<String, List<InputChangeListener>> propertyChangeListeners = new HashMap<>();

    public PropertiesPanelHolder(@NotNull Module module,
                                 @NotNull String componentPropertyPath,
                                 @NotNull ComponentDataHolder dataHolder,
                                 @NotNull List<PropertyDescriptor> descriptors,
                                 @Nullable FlowSnapshot snapshot) {
        this.module = module;
        this.snapshot = snapshot;
        this.dataHolder = dataHolder;
        this.componentPropertyPath = componentPropertyPath;
        this.descriptors.addAll(descriptors);

        setLayout(new GridBagLayout());
        setBorder(PANEL_BORDERS);
        initAccessors();
        renderProperties();
    }

    /**
     * Used by router component. The only property rendered by the router is the condition route table which
     * is a special, custom rendered property type.
     */
    public PropertiesPanelHolder(@NotNull Module module,
                                 @NotNull String componentPropertyPath,
                                 @NotNull ComponentDataHolder dataHolder,
                                 @Nullable FlowSnapshot snapshot) {
        this(module, componentPropertyPath, dataHolder, emptyList(), snapshot);
    }

    /**
     * Constructor used by a configuration panel dialog. The configuration panel dialog does not
     * immediately change the values on the Graph snapshot since it writes the values in a a config file.
     */
    public PropertiesPanelHolder(@NotNull Module module,
                                 @NotNull String componentPropertyPath,
                                 @NotNull ComponentDataHolder dataHolder,
                                 @NotNull List<PropertyDescriptor> descriptors) {
        this(module, componentPropertyPath, dataHolder, descriptors, null);
    }

    @Override
    public void subscribeOnPropertyChange(String propertyName, InputChangeListener inputChangeListener) {
        List<InputChangeListener> changeListenersForProperty =
                propertyChangeListeners.getOrDefault(propertyName, new ArrayList<>());
        changeListenersForProperty.add(inputChangeListener);
        propertyChangeListeners.put(propertyName, changeListenersForProperty);
    }

    @Override
    public <T> T propertyValueFrom(String propertyName) {
        // When an Enum does not have a default value, the property accessor
        // might be null. In this case its value would be null.
        PropertyAccessor propertyAccessor = changeAwarePropertyAccessor.get(propertyName);
        return propertyAccessor != null ? propertyAccessor.get() : null;
    }

    @Override
    public void addComponent(JComponentHolder componentHolder) {
        componentHolders.add(componentHolder);
    }

    @Override
    public Optional<JComponent> findComponentMatchingMetadata(BiPredicate<String, String> metadataPredicate) {
        for (JComponentHolder holder : componentHolders) {
            if (holder.matches(metadataPredicate)) {
                return Optional.of(holder.getComponent());
            }
        }
        return Optional.empty();
    }

    @Override
    public <T> void notifyPropertyChange(String propertyName, T object) {
        if (propertyChangeListeners.containsKey(propertyName)) {
            propertyChangeListeners.get(propertyName)
                    .forEach(inputChangeListener -> inputChangeListener.onChange(object));
        }
    }

    @Override
    public String componentPropertyPath() {
        return componentPropertyPath;
    }

    protected PropertyAccessor getAccessor(String propertyName, PropertyTypeDescriptor propertyType, ComponentDataHolder dataHolder) {
        return PropertyAccessorFactory.get()
                .typeDescriptor(propertyType)
                .propertyName(propertyName)
                .dataHolder(dataHolder)
                .snapshot(snapshot)
                .build();
    }

    private void initAccessors() {
        // We decorate each accessor with a property change decorator, which
        // notifies all the subscribers wishing to listen for a property change event
        // to be notified. This is needed for instance show/hide other properties using @When annotation.
        descriptors.forEach(propertyDescriptor -> {

            String propertyName = propertyDescriptor.getName();

            PropertyTypeDescriptor propertyType = propertyDescriptor.getType();

            PropertyAccessor propertyAccessor = getAccessor(propertyName, propertyType, dataHolder);

            PropertyAccessor propertyAccessorWrapper = new PropertyChangeNotifierDecorator(this, propertyAccessor);

            changeAwarePropertyAccessor.put(propertyName, propertyAccessorWrapper);

        });
    }

    private void renderProperties() {

        descriptors.forEach(descriptor -> {

            String propertyName = descriptor.getName();

            PropertyAccessor propertyAccessor = changeAwarePropertyAccessor.get(propertyName);

            PropertyTypeDescriptor propertyType = descriptor.getType();

            PropertyTypeRenderer renderer = PropertyTypeRendererFactory.get().from(propertyType);

            // We wrap the current context to make the next renderer aware of the path to
            // the property, starting from the component name.
            ContainerContext propertyAwareContext = ContainerContextDecorator.decorateForProperty(propertyName, this);

            JComponent renderedComponent = renderer.render(module, descriptor, propertyAccessor, propertyAwareContext);

            renderer.addToParent(this, renderedComponent, descriptor, propertyAwareContext);

        });
    }
}

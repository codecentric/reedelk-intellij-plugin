package de.codecentric.reedelk.plugin.editor.properties.context;

import de.codecentric.reedelk.plugin.editor.properties.commons.InputChangeListener;
import de.codecentric.reedelk.plugin.editor.properties.commons.JComponentHolder;
import de.codecentric.reedelk.plugin.graph.FlowGraph;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import de.codecentric.reedelk.plugin.graph.node.GraphNode;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentDataHolder;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyTypeDescriptor;
import de.codecentric.reedelk.plugin.commons.DebugControls;
import de.codecentric.reedelk.plugin.service.module.impl.component.ComponentContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.*;
import java.util.function.BiPredicate;

public class ContainerContext implements Disposable {

    private static final Logger LOG = Logger.getInstance(ContainerContext.class);

    final GraphNode node;
    final FlowSnapshot snapshot;
    private final String componentPropertyPath;

    private final List<JComponentHolder> componentHolders = new ArrayList<>();
    private final Map<String, PropertyAccessor> changeAwarePropertyAccessor = new HashMap<>();
    private final Map<String, List<InputChangeListener>> propertyChangeListeners = new HashMap<>();

    private boolean disposed = false;

    public ContainerContext(@Nullable FlowSnapshot snapshot,
                            @Nullable GraphNode node,
                            @NotNull String componentPropertyPath) {
        this.node = node;
        this.snapshot = snapshot;
        this.componentPropertyPath = componentPropertyPath;
        if (DebugControls.Properties.CONTAINER_CONTEXT_INFO) {
            LOG.info("CREATED_CONTAINER_CONTEXT: " + componentPropertyPath);
        }
    }

    public ComponentContext componentContext() {
        FlowGraph context = snapshot.getGraphOrThrowIfAbsent();
        return new ComponentContext(context, node);
    }

    public String componentPropertyPath() {
        return componentPropertyPath;
    }

    public <T> T propertyValueFrom(String propertyName) {
        // When an Enum does not have a default value, the property accessor
        // might be null. In this case its value would be null.
        String propertyPath = getPropertyPath(propertyName);
        PropertyAccessor propertyAccessor = changeAwarePropertyAccessor.get(propertyPath);
        return propertyAccessor != null ? propertyAccessor.get() : null;
    }

    public void addComponent(JComponentHolder componentHolder) {
        componentHolders.add(componentHolder);
    }

    public Optional<JComponent> findComponentMatchingMetadata(BiPredicate<String, String> metadataPredicate) {
        return componentHolders.stream()
                .filter(holder -> holder.matches(metadataPredicate))
                .map(JComponentHolder::getComponent)
                .findFirst();
    }

    public <T> void notifyPropertyChange(String propertyName, T object) {
        String propertyPath = getPropertyPath(propertyName);
        if (propertyChangeListeners.containsKey(propertyPath)) {
            propertyChangeListeners.get(propertyPath)
                    .forEach(inputChangeListener -> inputChangeListener.onChange(object));
        }
    }

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
    public PropertyAccessor propertyAccessorOf(@NotNull String propertyName,
                                               @NotNull PropertyTypeDescriptor propertyType,
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
    public String getPropertyPath(String propertyName) {
        // Property path is: com.my.component.fully.qualified.name#property1#subproperty1#subsubproperty1
        //  or: com.my.component.fully.qualified.name#property2#subproperty1
        return ComponentPropertyPath.join(componentPropertyPath(), propertyName);
    }

    @Override
    public void dispose() {
        if (!disposed) {
            componentHolders.clear();
            propertyChangeListeners.clear();
            changeAwarePropertyAccessor.clear();
            disposed = true;
            if (DebugControls.Properties.CONTAINER_CONTEXT_INFO) {
                LOG.info("DISPOSED_CONTAINER_CONTEXT: " + componentPropertyPath);
            }
        }
    }
}

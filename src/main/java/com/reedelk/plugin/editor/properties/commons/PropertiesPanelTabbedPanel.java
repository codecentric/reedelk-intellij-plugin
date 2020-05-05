package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentDataHolder;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyTypeDescriptor;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.editor.properties.context.ContainerContextDecorator;
import com.reedelk.plugin.graph.FlowSnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.intellij.util.ui.JBUI.emptyInsets;
import static com.reedelk.plugin.message.ReedelkBundle.message;

public class PropertiesPanelTabbedPanel extends DisposableTabbedPane {

    private final transient Module module;
    private final transient FlowSnapshot snapshot;
    private final transient ContainerContext context;
    private final transient ComponentData componentData;

    private PropertiesPanelTabbedPanel(@Nullable Module module,
                                       @Nullable FlowSnapshot snapshot,
                                       @NotNull ComponentData componentData,
                                       @NotNull ContainerContext context) {
        super(JTabbedPane.LEFT);
        this.module = module;
        this.snapshot = snapshot;
        this.context = context;
        this.componentData = componentData;
        setTabComponentInsets(emptyInsets());
    }

    public PropertiesPanelTabbedPanel(@NotNull Module module,
                                      @NotNull FlowSnapshot flowSnapshot,
                                      @NotNull ComponentData componentData,
                                      @NotNull Map<String, List<PropertyDescriptor>> propertiesByGroup,
                                      @NotNull ContainerContext context) {
        this(module, flowSnapshot, componentData, context);

        int count = 0;
        if (propertiesByGroup.isEmpty()) {
            // The component does not have any property defined (e.g Try-Catch, Fork ...)
            String tabName = message("properties.panel.tab.title.general");
            addPropertiesTab(tabName, PanelWithText.NoPropertiesPanel::new);
            setTabComponentAt(count, new TabLabelVertical(tabName));
            count++;
        } else {
            // We add for each tab group all the properties belonging to that group.
            for (Map.Entry<String, List<PropertyDescriptor>> entry : propertiesByGroup.entrySet()) {
                addPropertiesTab(entry.getKey(), entry.getValue());
                setTabComponentAt(count, new TabLabelVertical(entry.getKey()));
                count++;
            }
        }

        add(new HelpTab(componentData));
        setTabComponentAt(count, new TabLabelVertical(message("properties.panel.tab.title.help")));
    }

    // Used by specific and not generic components with their custom properties panel.
    // e.g Router or Flow Reference.
    public PropertiesPanelTabbedPanel(@NotNull ComponentData componentData,
                                      @NotNull Map<String, Supplier<JComponent>> componentByGroup,
                                      @NotNull ContainerContext context) {
        this(null, null, componentData, context);

        int count = 0;
        for (Map.Entry<String, Supplier<JComponent>> entry : componentByGroup.entrySet()) {
            addPropertiesTab(entry.getKey(), entry.getValue());
            setTabComponentAt(count, new TabLabelVertical(entry.getKey()));
            count++;
        }
        add(new HelpTab(componentData));
        setTabComponentAt(count, new TabLabelVertical(message("properties.panel.tab.title.help")));
    }

    private void addPropertiesTab(String key, Supplier<JComponent> componentSupplier) {
        GenericTab genericTab = new GenericTab(componentSupplier);
        addTab(key, genericTab);
    }

    private void addPropertiesTab(String propertyGroup, List<PropertyDescriptor> propertyDescriptors) {
        Supplier<JComponent> panelSupplier;
        if (isSingleTypeObject(propertyDescriptors)) {
            // Single type object: we *Unroll* all the object properties in the tab
            PropertyDescriptor propertyDescriptor = propertyDescriptors.get(0);
            PropertyTypeDescriptor type = propertyDescriptor.getType();
            ObjectDescriptor typeObjectDescriptor = (ObjectDescriptor) type;
            List<PropertyDescriptor> objectProperties = typeObjectDescriptor.getObjectProperties();

            ComponentDataHolder objectDataHolder = componentData.get(propertyDescriptor.getName());

            panelSupplier = () -> {
                // Lazy loading.

                // We are entering an object property: com.my.component#myObjectProperty1
                // We need a new context.
                ContainerContext newContext = ContainerContextDecorator.decorateForProperty(propertyDescriptor.getName(), context);

                JComponent panel = new PropertiesPanelHolder(module, newContext, objectDataHolder, objectProperties, snapshot);
                // Apply visibility specified on this object property with @When annotations.
                // Note that the visibility is computed using the *parent* context on this object panel holder.
                WhenVisibilityApplier.on(propertyDescriptor.getWhens(), context, panel);
                return panel;
            };

        } else {
            panelSupplier = () -> {
                // Lazy loading.
                // Visibility will be applied on each single property description in the panel holder.
                return new PropertiesPanelHolder(module, context, componentData, propertyDescriptors, snapshot);
            };
        }

        addPropertiesTab(propertyGroup, panelSupplier);
    }

    private boolean isSingleTypeObject(List<PropertyDescriptor> propertyDescriptors) {
        if (propertyDescriptors.size() == 1) {
            PropertyDescriptor propertyDescriptor = propertyDescriptors.get(0);
            PropertyTypeDescriptor type = propertyDescriptor.getType();
            return type instanceof ObjectDescriptor;
        }
        return false;
    }
}

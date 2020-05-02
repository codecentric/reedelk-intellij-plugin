package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.module.Module;
import com.intellij.util.ui.JBUI;
import com.reedelk.module.descriptor.model.component.ComponentDataHolder;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyTypeDescriptor;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.graph.FlowSnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class PropertiesPanelTabbedPanel extends DisposableTabbedPane {

    private final transient Module module;
    private final transient FlowSnapshot snapshot;
    private final transient ComponentData componentData;

    private PropertiesPanelTabbedPanel(@Nullable Module module,
                                       @Nullable FlowSnapshot snapshot,
                                       @NotNull ComponentData componentData) {
        super(JTabbedPane.LEFT);
        this.module = module;
        this.snapshot = snapshot;
        this.componentData = componentData;
        setTabComponentInsets(JBUI.emptyInsets());
    }

    public PropertiesPanelTabbedPanel(@NotNull Module module,
                                      @NotNull FlowSnapshot flowSnapshot,
                                      @NotNull ComponentData componentData,
                                      @NotNull Map<String, List<PropertyDescriptor>> propertiesByGroup) {
        this(module, flowSnapshot, componentData);

        int count = 0;
        if (propertiesByGroup.isEmpty()) {
            // The component does not have any property defined (e.g Try-Catch, Fork ...)
            String tabName = message("properties.panel.tab.title.general");
            addTabFromSupplier(tabName, PanelWithText.NoPropertiesPanel::new);
            setTabComponentAt(count, new TabLabelVertical(tabName));
            count++;
        } else {
            for (Map.Entry<String, List<PropertyDescriptor>> entry : propertiesByGroup.entrySet()) {
                addTabFromProperties(entry.getKey(), entry.getValue());
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
                                      @NotNull Map<String, Supplier<JComponent>> componentByGroup) {
        this(null, null, componentData);

        int count = 0;
        for (Map.Entry<String, Supplier<JComponent>> entry : componentByGroup.entrySet()) {
            addTabFromSupplier(entry.getKey(), entry.getValue());
            setTabComponentAt(count, new TabLabelVertical(entry.getKey()));
            count++;
        }
        add(new HelpTab(componentData));
        setTabComponentAt(count, new TabLabelVertical(message("properties.panel.tab.title.help")));
    }

    private void addTabFromSupplier(String key, Supplier<JComponent> componentSupplier) {
        GenericTab genericTab = new GenericTab(componentSupplier);
        addTab(key, genericTab);
    }

    private void addTabFromProperties(String key, List<PropertyDescriptor> propertyDescriptors) {
        if (isSingleTypeObject(propertyDescriptors)) {
            addTabFromSupplier(key, () -> {
                // *Unroll* all the object properties in the Tab
                PropertyDescriptor propertyDescriptor = propertyDescriptors.get(0);
                PropertyTypeDescriptor type = propertyDescriptor.getType();
                ObjectDescriptor typeObjectDescriptor = (ObjectDescriptor) type;
                List<PropertyDescriptor> objectProperties = typeObjectDescriptor.getObjectProperties();
                String fullyQualifiedName = typeObjectDescriptor.getTypeFullyQualifiedName();
                ComponentDataHolder objectDataHolder = componentData.get(propertyDescriptor.getName());
                return new PropertiesPanelHolder(module, fullyQualifiedName, objectDataHolder, objectProperties, snapshot);
            });
        } else {
            addTabFromSupplier(key, () -> {
                String fullyQualifiedName = componentData.getFullyQualifiedName();
                return new PropertiesPanelHolder(module, fullyQualifiedName, componentData, propertyDescriptors, snapshot);
            });
        }
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

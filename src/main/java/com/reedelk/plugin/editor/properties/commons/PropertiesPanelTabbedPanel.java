package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.module.Module;
import com.intellij.util.ui.JBEmptyBorder;
import com.reedelk.module.descriptor.model.ComponentDataHolder;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeDescriptor;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.component.ComponentData;
import com.reedelk.plugin.graph.FlowSnapshot;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.intellij.util.ui.JBUI.Borders;
import static com.reedelk.plugin.message.ReedelkBundle.message;

public class PropertiesPanelTabbedPanel extends DisposableTabbedPane {

    private static final JBEmptyBorder BORDER = Borders.empty(3, 5, 0, 0);

    private final Module module;
    private final FlowSnapshot snapshot;
    private final ComponentData componentData;

    public PropertiesPanelTabbedPanel(
            @NotNull Module module,
            @NotNull FlowSnapshot flowSnapshot,
            @NotNull ComponentData componentData,
            @NotNull Map<String, List<PropertyDescriptor>> propertiesByGroup) {
        super(JTabbedPane.LEFT);
        this.module = module;
        this.snapshot = flowSnapshot;
        this.componentData = componentData;

        int count = 0;

        if (propertiesByGroup.isEmpty()) {
            // The component does not have any property defined (e.g Try-Catch, Fork ...)
            String tabName = message("properties.panel.tab.title.general");
            addTabFromSupplier(tabName, PanelWithText.NoPropertiesPanel::new);
            setTabComponentAt(count, new TabLabel(tabName));
            count++;
        } else {
            for (Map.Entry<String, List<PropertyDescriptor>> entry : propertiesByGroup.entrySet()) {
                addTabFromProperties(entry.getKey(), entry.getValue());
                setTabComponentAt(count, new TabLabel(entry.getKey()));
                count++;
            }
        }

        add(new HelpTab(componentData));
        setTabComponentAt(count, new TabLabel(message("properties.panel.tab.title.help")));
    }

    public PropertiesPanelTabbedPanel(
            @NotNull ComponentData componentData,
            @NotNull Map<String, Supplier<JComponent>> componentsByGroup) {
        super(JTabbedPane.LEFT);
        this.module = null;
        this.snapshot = null;
        this.componentData = componentData;
        int count = 0;
        for (Map.Entry<String, Supplier<JComponent>> entry : componentsByGroup.entrySet()) {
            addTabFromSupplier(entry.getKey(), entry.getValue());
            setTabComponentAt(count, new TabLabel(entry.getKey()));
            count++;
        }
        add(new HelpTab(componentData));
        setTabComponentAt(count, new TabLabel(message("properties.panel.tab.title.help")));
    }

    private void addTabFromSupplier(String key, Supplier<JComponent> componentSupplier) {
        GenericTab genericTab = new GenericTab(componentSupplier);
        addTab(key, genericTab);
    }

    private void addTabFromProperties(String key, List<PropertyDescriptor> propertyDescriptors) {
        if (isSingleTypeObject(propertyDescriptors)) {
            addTabFromSupplier(key, () -> {
                PropertyDescriptor propertyDescriptor = propertyDescriptors.get(0);
                TypeDescriptor type = propertyDescriptor.getType();
                TypeObjectDescriptor typeObjectDescriptor = (TypeObjectDescriptor) type;
                List<PropertyDescriptor> objectProperties = typeObjectDescriptor.getObjectProperties();
                String fullyQualifiedName = typeObjectDescriptor.getTypeFullyQualifiedName();
                ComponentDataHolder objectDataHolder = componentData.get(propertyDescriptor.getName());
                return createPropertiesPanelHolder(objectProperties, fullyQualifiedName, objectDataHolder);
            });
        } else {
            addTabFromSupplier(key, () -> {
                String fullyQualifiedName = componentData.getFullyQualifiedName();
                return createPropertiesPanelHolder(propertyDescriptors, fullyQualifiedName, componentData);
            });
        }
    }

    @NotNull
    private PropertiesPanelHolder createPropertiesPanelHolder(@NotNull List<PropertyDescriptor> objectProperties,
                                                              @NotNull String fullyQualifiedName,
                                                              @NotNull ComponentDataHolder objectDataHolder) {
        PropertiesPanelHolder panel =
                new PropertiesPanelHolder(module, fullyQualifiedName, objectDataHolder, objectProperties, snapshot);
        panel.setBorder(BORDER);
        return panel;
    }

    private boolean isSingleTypeObject(List<PropertyDescriptor> propertyDescriptors) {
        if (propertyDescriptors.size() == 1) {
            PropertyDescriptor propertyDescriptor = propertyDescriptors.get(0);
            TypeDescriptor type = propertyDescriptor.getType();
            return type instanceof TypeObjectDescriptor;
        }
        return false;
    }
}

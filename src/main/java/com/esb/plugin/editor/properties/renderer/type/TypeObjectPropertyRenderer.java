package com.esb.plugin.editor.properties.renderer.type;

import com.esb.internal.commons.JsonParser;
import com.esb.internal.commons.StringUtils;
import com.esb.plugin.component.domain.ComponentDataHolder;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.esb.plugin.editor.properties.widget.*;
import com.esb.plugin.editor.properties.widget.input.ConfigSelector;
import com.esb.plugin.graph.FlowSnapshot;
import com.esb.plugin.service.module.ConfigService;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.esb.plugin.commons.Icons.Config.*;

public class TypeObjectPropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(Module module, ComponentPropertyDescriptor descriptor, PropertyAccessor accessor, FlowSnapshot snapshot) {
        TypeObjectDescriptor objectDescriptor = (TypeObjectDescriptor) descriptor.getPropertyType();
        return objectDescriptor.isShareable() ?
                renderShareable(module, objectDescriptor, accessor, snapshot) :
                renderInline(module, accessor, snapshot, objectDescriptor);
    }

    @NotNull
    private JComponent renderInline(Module module, PropertyAccessor propertyAccessor, FlowSnapshot snapshot, TypeObjectDescriptor objectDescriptor) {
        List<ComponentPropertyDescriptor> objectProperties = objectDescriptor.getObjectProperties();

        DefaultPropertiesPanel propertiesPanel = new DefaultPropertiesPanel();
        objectProperties.forEach(nestedPropertyDescriptor -> {

            final String displayName = nestedPropertyDescriptor.getDisplayName();
            final TypeDescriptor propertyType = nestedPropertyDescriptor.getPropertyType();

            // The accessor of type object returns a TypeObject map.
            ComponentDataHolder dataHolder = (ComponentDataHolder) propertyAccessor.get();

            PropertyAccessor nestedPropertyAccessor = PropertyAccessorFactory.get()
                    .typeDescriptor(nestedPropertyDescriptor.getPropertyType())
                    .dataHolder(dataHolder)
                    .propertyName(nestedPropertyDescriptor.getPropertyName())
                    .build();

            TypeRendererFactory typeRendererFactory = TypeRendererFactory.get();
            JComponent renderedComponent = typeRendererFactory.from(propertyType)
                    .render(module, nestedPropertyDescriptor, nestedPropertyAccessor, snapshot);

            FormBuilder.get()
                    .addLabel(displayName, propertiesPanel)
                    .addLastField(renderedComponent, propertiesPanel);
        });

        return propertiesPanel;
    }


    @NotNull
    private JComponent renderShareable(Module module, TypeObjectDescriptor typeObjectDescriptor, PropertyAccessor propertyAccessor, FlowSnapshot snapshot) {

        // TODO: Cannot use this one to render it....
        JComponent panel = renderInline(module, propertyAccessor, snapshot, typeObjectDescriptor);

        ActionableCommandButton editConfigCommand = new ActionableCommandButton("Edit", Edit);
        editConfigCommand.addListener(() ->
                SwingUtilities.invokeLater(() ->
                        new DialogEditConfiguration(module.getProject(), panel).show()));

        ActionableCommandButton addConfigCommand = new ActionableCommandButton("Add", Add);
        addConfigCommand.addListener(() ->
                SwingUtilities.invokeLater(() ->
                        new DialogAddConfiguration(module.getProject(), panel).show()));

        ActionableCommandButton deleteConfigCommand = new ActionableCommandButton("Delete", Delete);
        deleteConfigCommand.addListener(() ->
                SwingUtilities.invokeLater(() ->
                        new DialogRemoveConfiguration(module.getProject()).show()));

        JPanel controls = new JPanel();
        controls.add(editConfigCommand);
        controls.add(deleteConfigCommand);
        controls.add(addConfigCommand);

        List<ConfigMetadata> configMetadata =
                ConfigService.getInstance(module).listConfigs(typeObjectDescriptor.getTypeFullyQualifiedName());
        configMetadata.add(UNSELECTED_CONFIG);

        ComponentDataHolder dataHolder = (ComponentDataHolder) propertyAccessor.get();
        String configReference = dataHolder.get(JsonParser.Component.configRef());
        ConfigMetadata matchingMetadata = findMatchingMetadata(configMetadata, configReference);
        if (!configMetadata.contains(matchingMetadata)) {
            configMetadata.add(matchingMetadata);
        }

        ConfigSelector selector = new ConfigSelector(configMetadata);
        selector.setSelectedItem(matchingMetadata);
        selector.addSelectListener(configMetadata1 -> {
            // TODO: Complete Me
        });

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.add(selector, BorderLayout.CENTER);
        wrapper.add(controls, BorderLayout.EAST);
        return wrapper;
    }

    private static final String DEFAULT_CONFIG_REF = "";

    private static final ConfigMetadata UNSELECTED_CONFIG = new UnselectedConfigMetadata();

    static class UnresolvedConfigMetadata extends ConfigMetadata {
        UnresolvedConfigMetadata(String id) {
            super(id, String.format("Unresolved (%s)", id));
        }
    }

    static class UnselectedConfigMetadata extends ConfigMetadata {
        UnselectedConfigMetadata() {
            super(DEFAULT_CONFIG_REF, "<Not selected>");
        }
    }

    private ConfigMetadata findMatchingMetadata(List<ConfigMetadata> configsMetadata, String reference) {
        if (!StringUtils.isBlank(reference)) {
            return configsMetadata.stream()
                    .filter(configMetadata -> configMetadata.getId().equals(reference))
                    .findFirst()
                    .orElseGet(() -> new UnresolvedConfigMetadata(reference));
        }
        return UNSELECTED_CONFIG;
    }


}

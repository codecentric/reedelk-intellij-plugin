package com.esb.plugin.editor.properties.renderer;

import com.esb.internal.commons.JsonParser;
import com.esb.internal.commons.StringUtils;
import com.esb.plugin.commons.DefaultDescriptorDataValuesFiller;
import com.esb.plugin.component.domain.ComponentDataHolder;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.configuration.widget.ConfigControlPanel;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.esb.plugin.editor.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.editor.properties.widget.FormBuilder;
import com.esb.plugin.editor.properties.widget.input.ConfigSelector;
import com.esb.plugin.graph.serializer.JsonObjectFactory;
import com.esb.plugin.service.module.ConfigService;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.esb.internal.commons.JsonParser.Config;
import static com.esb.plugin.component.domain.TypeObjectDescriptor.TypeObject;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;

public class TypeObjectPropertyRenderer implements TypePropertyRenderer {

    private static final ConfigMetadata UNSELECTED_CONFIG;

    static {
        JSONObject unselectedConfigDefinition = JsonObjectFactory.newJSONObject();
        Config.id(TypeObject.DEFAULT_CONFIG_REF, unselectedConfigDefinition);
        Config.title("<Not selected>", unselectedConfigDefinition);
        UNSELECTED_CONFIG = new ConfigMetadata(unselectedConfigDefinition);
    }

    @Override
    public JComponent render(Module module, ComponentPropertyDescriptor descriptor, PropertyAccessor accessor) {
        TypeObjectDescriptor objectDescriptor = (TypeObjectDescriptor) descriptor.getPropertyType();
        return objectDescriptor.isShareable() ?
                renderShareable(module, objectDescriptor, accessor) :
                renderInline(module, accessor, objectDescriptor);
    }

    @NotNull
    private JComponent renderInline(Module module, PropertyAccessor propertyAccessor, TypeObjectDescriptor objectDescriptor) {
        List<ComponentPropertyDescriptor> objectProperties = objectDescriptor.getObjectProperties();

        // The accessor of type object returns a TypeObject map.
        ComponentDataHolder dataHolder = (ComponentDataHolder) propertyAccessor.get();

        // Fill Default Properties Values
        DefaultDescriptorDataValuesFiller.fill(dataHolder, objectProperties);

        DefaultPropertiesPanel propertiesPanel = new DefaultPropertiesPanel();
        objectProperties.forEach(nestedPropertyDescriptor -> {

            final String displayName = nestedPropertyDescriptor.getDisplayName();
            final TypeDescriptor propertyType = nestedPropertyDescriptor.getPropertyType();

            // We need a snapshot because changes needs to be written in the
            // flow itself since this is an inline object.
            PropertyAccessor nestedPropertyAccessor = PropertyAccessorFactory.get()
                    .typeDescriptor(nestedPropertyDescriptor.getPropertyType())
                    .propertyName(nestedPropertyDescriptor.getPropertyName())
                    .snapshot(propertyAccessor.getSnapshot())
                    .dataHolder(dataHolder)
                    .build();

            TypeRendererFactory typeRendererFactory = TypeRendererFactory.get();
            JComponent renderedComponent = typeRendererFactory.from(propertyType)
                    .render(module, nestedPropertyDescriptor, nestedPropertyAccessor);

            FormBuilder.get()
                    .addLabel(displayName, propertiesPanel)
                    .addLastField(renderedComponent, propertiesPanel);
        });

        return propertiesPanel;
    }

    @NotNull
    private JComponent renderShareable(Module module, TypeObjectDescriptor typeDescriptor, PropertyAccessor propertyAccessor) {
        // The Config Selector Combo
        ConfigSelector selector = new ConfigSelector();

        // The accessor of type object returns a TypeObject map
        // Since the object is shareable, it will contain config a config reference.
        ComponentDataHolder dataHolder = (ComponentDataHolder) propertyAccessor.get();

        // We create the accessor for the config reference
        PropertyAccessor configRefAccessor = PropertyAccessorFactory.get()
                .typeDescriptor(typeDescriptor)
                .propertyName(JsonParser.Component.configRef())
                .snapshot(propertyAccessor.getSnapshot())
                .dataHolder(dataHolder)
                .build();

        ConfigControlPanel configControlPanel = new ConfigControlPanel(module, typeDescriptor);
        configControlPanel.setAddActionListener(addedConfiguration -> {
            ConfigMetadata matchingMetadata =
                    updateMetadataOnSelector(module, selector, typeDescriptor, addedConfiguration.getId());
            configControlPanel.onSelect(matchingMetadata);
            configRefAccessor.set(matchingMetadata.getId());
        });

        configControlPanel.setDeleteActionListener(deletedConfiguration -> {
            ConfigMetadata matchingMetadata = updateMetadataOnSelector(module, selector, typeDescriptor, deletedConfiguration.getId());
            configControlPanel.onSelect(matchingMetadata);
        });

        String configReference = dataHolder.get(JsonParser.Component.configRef());
        ConfigMetadata matchingMetadata =
                updateMetadataOnSelector(module, selector, typeDescriptor, configReference);
        configControlPanel.onSelect(matchingMetadata);

        // We add the listener after, so that the first time we
        // don't update the graph json with the same info.
        selector.addSelectListener(selectedMetadata -> {
            configControlPanel.onSelect(selectedMetadata);
            configRefAccessor.set(selectedMetadata.getId());
        });

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.add(selector, CENTER);
        wrapper.add(configControlPanel, EAST);
        return wrapper;
    }

    private ConfigMetadata updateMetadataOnSelector(Module module, ConfigSelector selector, TypeObjectDescriptor typeObjectDescriptor, String targetReference) {
        List<ConfigMetadata> configMetadata =
                ConfigService.getInstance(module).listConfigsBy(typeObjectDescriptor.getTypeFullyQualifiedName());
        configMetadata.add(UNSELECTED_CONFIG);

        ConfigMetadata matchingMetadata = findMatchingMetadata(configMetadata, targetReference);
        if (!configMetadata.contains(matchingMetadata)) {
            configMetadata.add(matchingMetadata);
        }

        DefaultComboBoxModel<ConfigMetadata> metadata = new DefaultComboBoxModel<>();
        configMetadata.forEach(metadata::addElement);
        selector.setModel(metadata);
        selector.setSelectedItem(matchingMetadata);
        return matchingMetadata;
    }

    private ConfigMetadata findMatchingMetadata(List<ConfigMetadata> configsMetadata, String reference) {
        if (StringUtils.isBlank(reference)) return UNSELECTED_CONFIG;

        return configsMetadata.stream()
                .filter(configMetadata -> configMetadata.getId().equals(reference))
                .findFirst()
                .orElseGet(() -> {
                    JSONObject configDefinition = JsonObjectFactory.newJSONObject();
                    Config.title(String.format("Unresolved (%s)", reference), configDefinition);
                    Config.id(reference, configDefinition);
                    return new ConfigMetadata(configDefinition);
                });
    }
}

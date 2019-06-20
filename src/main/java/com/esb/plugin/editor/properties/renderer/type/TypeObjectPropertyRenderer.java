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
import com.esb.plugin.service.module.ConfigService;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.esb.plugin.commons.Icons.Config.*;
import static javax.swing.SwingUtilities.invokeLater;

public class TypeObjectPropertyRenderer implements TypePropertyRenderer {

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

        DefaultPropertiesPanel propertiesPanel = new DefaultPropertiesPanel();
        objectProperties.forEach(nestedPropertyDescriptor -> {

            final String displayName = nestedPropertyDescriptor.getDisplayName();
            final TypeDescriptor propertyType = nestedPropertyDescriptor.getPropertyType();

            // The accessor of type object returns a TypeObject map.
            ComponentDataHolder dataHolder = (ComponentDataHolder) propertyAccessor.get();

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

        Project project = module.getProject();

        ActionableCommandButton editConfigCommand = new ActionableCommandButton("Edit", Edit);
        editConfigCommand.addListener((selectedConfig) -> invokeLater(() -> {
            DialogEditConfiguration dialogEditConfiguration = new DialogEditConfiguration(module, typeDescriptor, selectedConfig);
            if (dialogEditConfiguration.showAndGet()) {
                dialogEditConfiguration.save();
            }
        }));

        ActionableCommandButton addConfigCommand = new ActionableCommandButton("Add", Add);
        addConfigCommand.addListener((selectedConfig) -> invokeLater(() -> {
            DialogAddConfiguration dialogAddConfiguration = new DialogAddConfiguration(module, typeDescriptor);
            if (dialogAddConfiguration.showAndGet()) {
                dialogAddConfiguration.save();
            }
        }));

        ActionableCommandButton deleteConfigCommand = new ActionableCommandButton("Delete", Delete);
        deleteConfigCommand.addListener((selectedConfig) -> invokeLater(() -> {
            DialogRemoveConfiguration dialogRemoveConfiguration = new DialogRemoveConfiguration(project);
            if (dialogRemoveConfiguration.showAndGet()) {
                dialogRemoveConfiguration.delete();
            }
        }));


        // Get all the configurations for the given implementor's fully qualified name class
        List<ConfigMetadata> configMetadata =
                ConfigService.getInstance(module).listConfigs(typeDescriptor.getTypeFullyQualifiedName());
        configMetadata.add(UNSELECTED_CONFIG);

        // The accessor of type object returns a TypeObject map : in this case it will contain config ref
        ComponentDataHolder dataHolder = (ComponentDataHolder) propertyAccessor.get();
        String configReference = dataHolder.get(JsonParser.Component.configRef());
        ConfigMetadata matchingMetadata = findMatchingMetadata(configMetadata, configReference);
        if (!configMetadata.contains(matchingMetadata)) {
            configMetadata.add(matchingMetadata);
        }


        PropertyAccessor configRefAccessor = PropertyAccessorFactory.get()
                .typeDescriptor(typeDescriptor)
                .propertyName(JsonParser.Component.configRef())
                .snapshot(propertyAccessor.getSnapshot())
                .dataHolder(dataHolder)
                .build();


        ConfigSelector selector = new ConfigSelector(configMetadata);
        selector.setSelectedItem(matchingMetadata);
        editConfigCommand.onSelect(matchingMetadata);
        deleteConfigCommand.onSelect(matchingMetadata);

        selector.addSelectListener(selectedMetadata -> {
            editConfigCommand.onSelect(selectedMetadata);
            deleteConfigCommand.onSelect(selectedMetadata);
            configRefAccessor.set(selectedMetadata.getId());
        });


        JPanel controls = new JPanel();
        controls.add(editConfigCommand);
        controls.add(deleteConfigCommand);
        controls.add(addConfigCommand);


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

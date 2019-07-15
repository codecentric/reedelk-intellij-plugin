package com.esb.plugin.editor.properties.renderer;

import com.esb.internal.commons.JsonParser;
import com.esb.internal.commons.StringUtils;
import com.esb.plugin.commons.DefaultDescriptorDataValuesFiller;
import com.esb.plugin.component.domain.ComponentDataHolder;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.configuration.widget.ActionAddConfiguration;
import com.esb.plugin.configuration.widget.ActionDeleteConfiguration;
import com.esb.plugin.configuration.widget.ConfigControlPanel;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.esb.plugin.editor.properties.widget.ContainerFactory;
import com.esb.plugin.editor.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.editor.properties.widget.FormBuilder;
import com.esb.plugin.editor.properties.widget.PropertyPanelContext;
import com.esb.plugin.editor.properties.widget.input.ConfigSelector;
import com.esb.plugin.service.module.ConfigService;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.esb.internal.commons.JsonParser.Config;
import static com.esb.plugin.component.domain.TypeObjectDescriptor.TypeObject;
import static com.intellij.openapi.ui.MessageType.WARNING;
import static com.intellij.openapi.ui.popup.Balloon.Position;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;

public class TypeObjectPropertyRenderer implements TypePropertyRenderer {

    private static final ConfigMetadata UNSELECTED_CONFIG;

    static {
        TypeObject unselectedConfigDefinition = new TypeObject();
        unselectedConfigDefinition.set(Config.id(), TypeObject.DEFAULT_CONFIG_REF);
        unselectedConfigDefinition.set(Config.title(), "<Not selected>");
        UNSELECTED_CONFIG = new ConfigMetadata(unselectedConfigDefinition);
    }

    @Override
    public JComponent render(Module module, ComponentPropertyDescriptor descriptor, PropertyAccessor accessor, PropertyPanelContext context) {
        TypeObjectDescriptor objectDescriptor = (TypeObjectDescriptor) descriptor.getPropertyType();
        return objectDescriptor.isShareable() ?
                renderShareable(module, objectDescriptor, accessor) :
                renderInline(module, accessor, objectDescriptor);
    }

    @Override
    public void addToParent(JComponent parent, JComponent rendered, String label) {
        // If the property type is a complex object, we wrap it in a
        // bordered box with title the name of the object property.
        JBPanel wrappedRenderedComponent = ContainerFactory
                .createObjectTypeContainer(label, rendered);
        FormBuilder.get()
                .addLastField(wrappedRenderedComponent, parent);
    }

    @NotNull
    private JComponent renderInline(Module module, PropertyAccessor propertyAccessor, TypeObjectDescriptor objectDescriptor) {
        List<ComponentPropertyDescriptor> objectProperties = objectDescriptor.getObjectProperties();

        // The accessor of type object returns a TypeObject map.
        ComponentDataHolder dataHolder = propertyAccessor.get();

        // Fill Default Properties Values
        DefaultDescriptorDataValuesFiller.fill(dataHolder, objectProperties);

        DefaultPropertiesPanel propertiesPanel = new DefaultPropertiesPanel(dataHolder, objectProperties, propertyAccessor.getSnapshot());
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
                    .render(module, nestedPropertyDescriptor, nestedPropertyAccessor, propertiesPanel);

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
        ComponentDataHolder dataHolder = propertyAccessor.get();

        // We create the accessor for the config reference
        PropertyAccessor configRefAccessor = PropertyAccessorFactory.get()
                .typeDescriptor(typeDescriptor)
                .propertyName(JsonParser.Component.configRef())
                .snapshot(propertyAccessor.getSnapshot())
                .dataHolder(dataHolder)
                .build();

        ConfigControlPanel configControlPanel = new ConfigControlPanel(module, typeDescriptor);
        configControlPanel.setAddActionListener(new ActionAddConfiguration.AddCompleteListener() {
            @Override
            public void onAddedConfiguration(ConfigMetadata addedConfiguration) {
                ConfigMetadata matchingMetadata =
                        updateMetadataOnSelector(module, selector, typeDescriptor, addedConfiguration.getId());
                configControlPanel.onSelect(matchingMetadata);
                configRefAccessor.set(matchingMetadata.getId());
            }

            @Override
            public void onAddedConfigurationError(Exception exception, ConfigMetadata metadata) {
                displayErrorPopup(exception, configControlPanel);
            }
        });

        configControlPanel.setDeleteActionListener(new ActionDeleteConfiguration.DeleteCompleteListener() {
            @Override
            public void onDeletedConfiguration(ConfigMetadata deletedConfiguration) {
                ConfigMetadata matchingMetadata = updateMetadataOnSelector(module, selector, typeDescriptor, deletedConfiguration.getId());
                configControlPanel.onSelect(matchingMetadata);
            }

            @Override
            public void onDeletedConfigurationError(Exception exception, ConfigMetadata configMetadata) {
                displayErrorPopup(exception, configControlPanel);
            }
        });

        configControlPanel.setEditActionListener((exception, metadata) ->
                displayErrorPopup(exception, configControlPanel));

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

    private void displayErrorPopup(Exception exception, ConfigControlPanel configControlPanel) {
        String errorMessage = exception.getMessage();
        String content = String.format("<p><b>An error has occurred:</b><p>%s</p>", errorMessage);
        JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(content, WARNING, null)
                .createBalloon()
                .show(RelativePoint.getCenterOf(configControlPanel), Position.above);
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
                    TypeObject unselectedConfigDefinition = new TypeObject();
                    unselectedConfigDefinition.set(Config.id(), reference);
                    unselectedConfigDefinition.set(Config.title(), String.format("Unresolved (%s)", reference));
                    return new ConfigMetadata(unselectedConfigDefinition);
                });
    }
}

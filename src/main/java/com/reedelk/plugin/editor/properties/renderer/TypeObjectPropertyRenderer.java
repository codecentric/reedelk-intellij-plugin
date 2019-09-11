package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.awt.RelativePoint;
import com.reedelk.plugin.commons.Labels;
import com.reedelk.plugin.component.domain.ComponentDataHolder;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeDescriptor;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.reedelk.plugin.editor.properties.configuration.ActionAddConfiguration;
import com.reedelk.plugin.editor.properties.configuration.ActionDeleteConfiguration;
import com.reedelk.plugin.editor.properties.configuration.ConfigControlPanel;
import com.reedelk.plugin.editor.properties.widget.*;
import com.reedelk.plugin.editor.properties.widget.input.ConfigSelector;
import com.reedelk.plugin.service.module.ConfigService;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.commons.JsonParser;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.intellij.openapi.ui.MessageType.WARNING;
import static com.intellij.openapi.ui.popup.Balloon.Position;
import static com.reedelk.plugin.component.domain.Shared.NO;
import static com.reedelk.plugin.component.domain.Shared.YES;
import static com.reedelk.plugin.component.domain.TypeObjectDescriptor.TypeObject;
import static com.reedelk.runtime.commons.JsonParser.Config;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;

public class TypeObjectPropertyRenderer extends AbstractTypePropertyRenderer {

    private static final ConfigMetadata UNSELECTED_CONFIG;
    static {
        TypeObject unselectedConfigDefinition = new TypeObject();
        unselectedConfigDefinition.set(Config.id(), TypeObject.DEFAULT_CONFIG_REF);
        unselectedConfigDefinition.set(Config.title(), Labels.CONFIG_NOT_SELECTED_ITEM);
        UNSELECTED_CONFIG = new ConfigMetadata(unselectedConfigDefinition);
    }

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull ComponentPropertyDescriptor descriptor,
                             @NotNull PropertyAccessor accessor,
                             @NotNull ContainerContext context) {
        TypeObjectDescriptor objectDescriptor = descriptor.getPropertyType();
        return YES.equals(objectDescriptor.getShared()) ?
                renderShareable(module, descriptor, accessor) :
                renderInline(module, accessor, objectDescriptor);
    }

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull ComponentPropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {
        TypeObjectDescriptor objectDescriptor = descriptor.getPropertyType();
        if (NO.equals(objectDescriptor.getShared())) {
            addToParentInline(parent, rendered, descriptor, context);
        } else {
            super.addToParent(parent, rendered, descriptor, context);
        }
        // Add the component to the context
        context.addComponent(new JComponentHolder(rendered));
    }

    @NotNull
    private JComponent renderInline(Module module, PropertyAccessor propertyAccessor, TypeObjectDescriptor objectDescriptor) {
        List<ComponentPropertyDescriptor> objectProperties = objectDescriptor.getObjectProperties();

        // The accessor of type object returns a TypeObject map, if it  is empty...
        ComponentDataHolder dataHolder = propertyAccessor.get();

        PropertiesPanelHolder propertiesPanel = new PropertiesPanelHolder(dataHolder, objectProperties, propertyAccessor.getSnapshot());
        objectProperties.forEach(objectProperty -> {

            String propertyName = objectProperty.getPropertyName();

            PropertyAccessor nestedPropertyAccessor = propertiesPanel.getAccessor(propertyName);

            TypeDescriptor propertyType = objectProperty.getPropertyType();

            TypePropertyRenderer renderer = TypeRendererFactory.get().from(propertyType);

            JComponent renderedComponent = renderer.render(module, objectProperty, nestedPropertyAccessor, propertiesPanel);

            renderer.addToParent(propertiesPanel, renderedComponent, objectProperty, propertiesPanel);
        });

        return propertiesPanel;
    }

    @NotNull
    private JComponent renderShareable(Module module, ComponentPropertyDescriptor descriptor, PropertyAccessor propertyAccessor) {
        // The Config Selector Combo
        ConfigSelector selector = new ConfigSelector();

        // The accessor of type object returns a TypeObject map
        // Since the object is shareable, it will contain a config reference.
        ComponentDataHolder dataHolder = propertyAccessor.get();

        // We create the accessor for the config reference
        PropertyAccessor configRefAccessor = PropertyAccessorFactory.get()
                .typeDescriptor(descriptor.getPropertyType())
                .propertyName(JsonParser.Component.configRef())
                .snapshot(propertyAccessor.getSnapshot())
                .dataHolder(dataHolder)
                .build();

        ConfigControlPanel configControlPanel = new ConfigControlPanel(module, descriptor.getPropertyType());
        configControlPanel.setAddActionListener(new ActionAddConfiguration.AddCompleteListener() {
            @Override
            public void onAddedConfiguration(ConfigMetadata addedConfiguration) {
                ConfigMetadata matchingMetadata =
                        updateMetadataOnSelector(module, selector, descriptor, addedConfiguration.getId());
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
                ConfigMetadata matchingMetadata = updateMetadataOnSelector(module, selector, descriptor, deletedConfiguration.getId());
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
                updateMetadataOnSelector(module, selector, descriptor, configReference);
        configControlPanel.onSelect(matchingMetadata);

        // We add the listener after, so that the first time we
        // don't update the graph json with the same info.
        selector.addSelectListener(selectedMetadata -> {
            configControlPanel.onSelect(selectedMetadata);
            configRefAccessor.set(selectedMetadata.getId());
        });

        JPanel wrapper = new DisposablePanel();
        wrapper.setLayout(new BorderLayout());
        wrapper.add(selector, CENTER);
        wrapper.add(configControlPanel, EAST);
        return wrapper;
    }

    // TODO: Extract in an utility class to show error messages...
    private void displayErrorPopup(Exception exception, ConfigControlPanel configControlPanel) {
        String errorMessage = exception.getMessage();
        String content = String.format(Labels.BALLOON_EDIT_CONFIG_ERROR, errorMessage);
        JBPopupFactory.getInstance().createHtmlTextBalloonBuilder(content, WARNING, null)
                .createBalloon()
                .show(RelativePoint.getCenterOf(configControlPanel), Position.above);
    }

    private ConfigMetadata updateMetadataOnSelector(Module module, ConfigSelector selector, ComponentPropertyDescriptor typeObjectDescriptor, String targetReference) {
        List<ConfigMetadata> configMetadata =
                ConfigService.getInstance(module).listConfigsBy(typeObjectDescriptor.getPropertyType());
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

    private void addToParentInline(@NotNull JComponent parent, @NotNull JComponent rendered, @NotNull ComponentPropertyDescriptor descriptor, @NotNull ContainerContext context) {
        // If the property type is a complex object (not shared), we wrap it in a
        // bordered box with title the name of the object property.
        TypeObjectDescriptor objectDescriptor = descriptor.getPropertyType();

        DisposablePanel wrappedRenderedComponent =
                ContainerFactory.createObjectTypeContainer(descriptor.getDisplayName(), objectDescriptor, rendered);

        // If the property has any 'when' condition, we apply listener/s to make it
        // visible (or not) when the condition is met (or not).
        applyWhenVisibilityConditions(descriptor.getWhenDefinitions(), context, wrappedRenderedComponent);

        // Add the component to the parent container.
        FormBuilder.get()
                .addLastField(wrappedRenderedComponent, parent);
    }
}

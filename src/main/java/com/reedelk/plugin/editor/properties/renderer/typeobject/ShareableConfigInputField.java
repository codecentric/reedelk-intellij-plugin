package com.reedelk.plugin.editor.properties.renderer.typeobject;

import com.intellij.openapi.module.Module;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.module.descriptor.model.component.ComponentDataHolder;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.commons.PopupUtils;
import com.reedelk.plugin.commons.TypeObjectFactory;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ComboActionsPanel;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.renderer.typeobject.configuration.ConfigControlPanel;
import com.reedelk.plugin.editor.properties.renderer.typeobject.configuration.ConfigSelectorCombo;
import com.reedelk.plugin.service.module.ConfigurationService;
import com.reedelk.plugin.service.module.impl.configuration.ConfigMetadata;
import com.reedelk.plugin.service.module.impl.configuration.ConfigurationServiceImpl;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.commons.JsonParser;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.plugin.topic.ReedelkTopics.TOPIC_CONFIG_CHANGE;
import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;

class ShareableConfigInputField extends DisposablePanel implements ConfigurationServiceImpl.ConfigChangeListener {

    private final transient Module module;
    private final transient ContainerContext context;
    private final transient MessageBusConnection connect;
    private final transient PropertyAccessor propertyAccessor;
    private final transient PropertyDescriptor descriptor;
    private final transient ComponentDataHolder referenceDataHolder;

    private final ConfigSelectorCombo configSelectorCombo;
    private final String currentInputFieldTypeFullyQualifiedName;
    private final ComboActionsPanel<ConfigMetadata> configActionsPanel;


    ShareableConfigInputField(@NotNull Module module,
                              @NotNull ComponentDataHolder referenceDataHolder,
                              @NotNull PropertyDescriptor descriptor,
                              @NotNull PropertyAccessor propertyAccessor,
                              @NotNull ContainerContext context) {

        this.module = module;
        this.context = context;
        this.descriptor = descriptor;
        this.referenceDataHolder = referenceDataHolder;
        this.connect = module.getMessageBus().connect();
        this.connect.subscribe(TOPIC_CONFIG_CHANGE, this);

        this.propertyAccessor = propertyAccessor;

        // The Config Selector Combo
        this.configSelectorCombo = new ConfigSelectorCombo();
        this.configActionsPanel = new ConfigControlPanel(module, descriptor.getType());

        setLayout(new BorderLayout());
        add(configSelectorCombo, CENTER);
        add(configActionsPanel, EAST);

        ObjectDescriptor typeObjectDescriptor = descriptor.getType();
        this.currentInputFieldTypeFullyQualifiedName = typeObjectDescriptor.getTypeFullyQualifiedName();
        ConfigurationService.getInstance(module).loadConfigurationsBy(typeObjectDescriptor);
    }

    @Override
    public void onConfigurationsReady(String typeObjectFullyQualifiedName, Collection<ConfigMetadata> configurations) {
        // The event is might refer to another Config Input Field in the same Panel therefore we need to check if
        // the fully qualified name of the object type this event is referring to can be applied to this input field.
        // This happens when you have two @Shared objects in the same Property Panel. When one input field requests
        // configurations the topic TOPIC_CONFIG_CHANGE is subscribed by both input fields.
        // Therefore here we only update the configurations if and only if the event refers to configurations for
        // this object type fully qualified name.
        if (Objects.equals(currentInputFieldTypeFullyQualifiedName, typeObjectFullyQualifiedName)) {
            String currentSelectedScriptReference = propertyAccessor.get();
            ConfigMetadata matchingMetadata = findMatchingMetadata(configurations, currentSelectedScriptReference);
            updateWith(configurations, matchingMetadata);
        }
    }

    @Override
    public void onAddSuccess(String typeObjectFullyQualifiedName, ConfigMetadata configMetadata) {
        // Check if event refers to the current type object fully qualified name
        if (Objects.equals(currentInputFieldTypeFullyQualifiedName, typeObjectFullyQualifiedName)) {
            setPropertyAccessorValue(configMetadata.getId());
            ConfigurationService.getInstance(module).loadConfigurationsBy(descriptor.getType());
        }
    }

    @Override
    public void onRemoveSuccess(String typeObjectFullyQualifiedName) {
        // Check if event refers to the current type object fully qualified name
        if (Objects.equals(currentInputFieldTypeFullyQualifiedName, typeObjectFullyQualifiedName)) {
            // Nothing is selected.
            setPropertyAccessorValue(EMPTY);
            ConfigurationService.getInstance(module).loadConfigurationsBy(descriptor.getType());
        }
    }

    @Override
    public void onSaveError(String typeObjectFullyQualifiedName, Exception exception) {
        // Check if event refers to the current type object fully qualified name
        if (Objects.equals(currentInputFieldTypeFullyQualifiedName, typeObjectFullyQualifiedName)) {
            PopupUtils.error(exception, configActionsPanel);
        }
    }

    @Override
    public void onAddError(String typeObjectFullyQualifiedName, Exception exception) {
        // Check if event refers to the current type object fully qualified name
        if (Objects.equals(currentInputFieldTypeFullyQualifiedName, typeObjectFullyQualifiedName)) {
            PopupUtils.error(exception, configActionsPanel);
        }
    }

    @Override
    public void onRemoveError(String typeObjectFullyQualifiedName, Exception exception) {
        // Check if event refers to the current type object fully qualified name
        if (Objects.equals(currentInputFieldTypeFullyQualifiedName, typeObjectFullyQualifiedName)) {
            PopupUtils.error(exception, configActionsPanel);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        connect.disconnect();
    }

    private void updateWith(Collection<ConfigMetadata> configMetadata, ConfigMetadata selected) {
        // Prepare model
        List<ConfigMetadata> updatedConfigurations = new ArrayList<>(configMetadata);
        updatedConfigurations.add(UNSELECTED_CONFIG);
        if (!updatedConfigurations.contains(selected)) {
            updatedConfigurations.add(selected);
        }
        DefaultComboBoxModel<ConfigMetadata> comboModel = new DefaultComboBoxModel<>();
        updatedConfigurations.forEach(comboModel::addElement);

        SwingUtilities.invokeLater(() -> {
            // We must remove any previous listener.
            configSelectorCombo.removeListener();

            // Update the model
            configSelectorCombo.setModel(comboModel);
            configSelectorCombo.setSelectedItem(selected);
            configActionsPanel.select(selected);

            // Add back the listener
            configSelectorCombo.addListener(value -> {
                ConfigMetadata selectedConfig = (ConfigMetadata) value;
                configActionsPanel.select(selectedConfig);
                setPropertyAccessorValue(selectedConfig.getId());
            });
        });
    }

    private ConfigMetadata findMatchingMetadata(Collection<ConfigMetadata> configsMetadata, String reference) {
        if (StringUtils.isBlank(reference)) return UNSELECTED_CONFIG;
        return configsMetadata.stream()
                .filter(configMetadata -> configMetadata.getId().equals(reference))
                .findFirst()
                .orElseGet(() -> {
                    ObjectDescriptor.TypeObject unselectedConfigDefinition = TypeObjectFactory.newInstance();
                    unselectedConfigDefinition.set(JsonParser.Config.id(), reference);
                    unselectedConfigDefinition.set(JsonParser.Config.title(), String.format("Unresolved (%s)", reference));
                    return new ConfigMetadata(unselectedConfigDefinition);
                });
    }

    private void setPropertyAccessorValue(String configReference) {
        propertyAccessor.set(configReference);
        // If the selection has changed, we must notify all the
        // context subscribers that the property has changed.
        context.notifyPropertyChanged(descriptor.getName(), referenceDataHolder);
    }

    private static final ConfigMetadata UNSELECTED_CONFIG;
    static {
        ObjectDescriptor.TypeObject unselectedConfigDefinition = TypeObjectFactory.newInstance();
        unselectedConfigDefinition.set(JsonParser.Config.id(), ObjectDescriptor.TypeObject.DEFAULT_CONFIG_REF);
        unselectedConfigDefinition.set(JsonParser.Config.title(), message("config.not.selected"));
        UNSELECTED_CONFIG = new ConfigMetadata(unselectedConfigDefinition);
    }
}

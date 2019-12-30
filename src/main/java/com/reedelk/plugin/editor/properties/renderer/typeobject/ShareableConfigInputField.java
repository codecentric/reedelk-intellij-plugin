package com.reedelk.plugin.editor.properties.renderer.typeobject;

import com.intellij.openapi.module.Module;
import com.intellij.util.messages.MessageBusConnection;
import com.reedelk.module.descriptor.model.ComponentDataHolder;
import com.reedelk.module.descriptor.model.ComponentPropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.commons.PopupUtils;
import com.reedelk.plugin.commons.TypeObjectFactory;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.renderer.typeobject.configuration.ConfigControlPanel;
import com.reedelk.plugin.editor.properties.renderer.typeobject.configuration.ConfigSelectorCombo;
import com.reedelk.plugin.service.module.ConfigService;
import com.reedelk.plugin.service.module.impl.configuration.ConfigMetadata;
import com.reedelk.plugin.service.module.impl.configuration.ConfigServiceImpl;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.commons.JsonParser;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.plugin.topic.ReedelkTopics.TOPIC_CONFIG_CHANGE;
import static com.reedelk.runtime.api.commons.StringUtils.EMPTY;
import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;

class ShareableConfigInputField extends DisposablePanel implements ConfigServiceImpl.ConfigChangeListener {

    private final transient Module module;
    private final transient ContainerContext context;
    private final transient MessageBusConnection connect;
    private final transient PropertyAccessor propertyAccessor;
    private final transient ComponentPropertyDescriptor descriptor;
    private final transient ComponentDataHolder referenceDataHolder;

    private final ConfigControlPanel configActionsPanel;
    private final ConfigSelectorCombo configSelectorCombo;


    ShareableConfigInputField(@NotNull Module module,
                              @NotNull ComponentDataHolder referenceDataHolder,
                              @NotNull ComponentPropertyDescriptor descriptor,
                              @NotNull PropertyAccessor propertyAccessor,
                              @NotNull ContainerContext context) {

        this.module = module;
        this.referenceDataHolder = referenceDataHolder;
        this.context = context;
        this.descriptor = descriptor;
        this.connect = module.getMessageBus().connect();
        this.connect.subscribe(TOPIC_CONFIG_CHANGE, this);

        this.propertyAccessor = propertyAccessor;

        // The Config Selector Combo
        this.configSelectorCombo = new ConfigSelectorCombo();
        this.configActionsPanel = new ConfigControlPanel(module, descriptor.getPropertyType());

        setLayout(new BorderLayout());
        add(configSelectorCombo, CENTER);
        add(configActionsPanel, EAST);

        ConfigService.getInstance(module).fetchConfigurationsBy(descriptor.getPropertyType());
    }

    @Override
    public void onConfigs(Collection<ConfigMetadata> configurations) {
        String currentSelectedScriptReference = propertyAccessor.get();
        ConfigMetadata matchingMetadata = findMatchingMetadata(configurations, currentSelectedScriptReference);
        updateWith(configurations, matchingMetadata);
    }

    @Override
    public void onAddSuccess(ConfigMetadata configMetadata) {
        propertyAccessor.set(configMetadata.getId());
        ConfigService.getInstance(module).fetchConfigurationsBy(descriptor.getPropertyType());
    }

    @Override
    public void onRemoveSuccess() {
        // Nothing is selected.
        propertyAccessor.set(EMPTY);
        ConfigService.getInstance(module).fetchConfigurationsBy(descriptor.getPropertyType());
    }

    @Override
    public void onSaveError(Exception exception) {
        PopupUtils.error(exception, configActionsPanel);
    }

    @Override
    public void onAddError(Exception exception) {
        PopupUtils.error(exception, configActionsPanel);
    }

    @Override
    public void onRemoveError(Exception exception) {
        PopupUtils.error(exception, configActionsPanel);
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
            configActionsPanel.onSelect(selected);

            // Add back the listener
            configSelectorCombo.addListener(value -> {
                propertyAccessor.set(((ConfigMetadata) value).getId());
                configActionsPanel.onSelect((ConfigMetadata) value);

                // If the selection has changed, we must notify all the
                // context subscribers that the property has changed.
                context.notifyPropertyChanged(descriptor.getPropertyName(), referenceDataHolder);
            });
        });
    }

    private ConfigMetadata findMatchingMetadata(Collection<ConfigMetadata> configsMetadata, String reference) {
        if (StringUtils.isBlank(reference)) return UNSELECTED_CONFIG;
        return configsMetadata.stream()
                .filter(configMetadata -> configMetadata.getId().equals(reference))
                .findFirst()
                .orElseGet(() -> {
                    TypeObjectDescriptor.TypeObject unselectedConfigDefinition = TypeObjectFactory.newInstance();
                    unselectedConfigDefinition.set(JsonParser.Config.id(), reference);
                    unselectedConfigDefinition.set(JsonParser.Config.title(), String.format("Unresolved (%s)", reference));
                    return new ConfigMetadata(unselectedConfigDefinition);
                });
    }

    private static final ConfigMetadata UNSELECTED_CONFIG;
    static {
        TypeObjectDescriptor.TypeObject unselectedConfigDefinition = TypeObjectFactory.newInstance();
        unselectedConfigDefinition.set(JsonParser.Config.id(), TypeObjectDescriptor.TypeObject.DEFAULT_CONFIG_REF);
        unselectedConfigDefinition.set(JsonParser.Config.title(), message("config.not.selected"));
        UNSELECTED_CONFIG = new ConfigMetadata(unselectedConfigDefinition);
    }
}

package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

import com.intellij.openapi.module.Module;
import com.intellij.util.ui.JBUI;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.commons.InitValuesFiller;
import com.reedelk.plugin.commons.PredefinedPropertyDescriptor;
import com.reedelk.plugin.editor.properties.commons.*;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.service.module.impl.configuration.ConfigMetadata;

import javax.swing.*;
import java.awt.*;
import java.util.List;

class ConfigPropertiesPanel extends DisposablePanel {

    private ContainerContext context;

    ConfigPropertiesPanel(Module module, ConfigMetadata configMetadata, ObjectDescriptor objectDescriptor, boolean isNewConfig) {

        List<PropertyDescriptor> descriptors = objectDescriptor.getObjectProperties();

        if (isNewConfig) {
            // Fill Default Properties Values
            InitValuesFiller.fill(configMetadata, descriptors);
        }

        ConfigMetadataHeaderPanel headerPanel = new ConfigMetadataHeaderPanel(configMetadata, isNewConfig);

        String componentFullyQualifiedName = objectDescriptor.getTypeFullyQualifiedName();

        context = new ContainerContext(null, null, componentFullyQualifiedName);

        PropertiesPanelHolder propertiesPanel =
                new PropertiesPanelHolder(module, context, configMetadata, descriptors);

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(headerPanel);
        add(propertiesPanel);
    }

    @Override
    public void dispose() {
        super.dispose();
        DisposableUtils.dispose(context);
        this.context = null;
    }

    static class ConfigMetadataHeaderPanel extends DisposablePanel {

        ConfigMetadataHeaderPanel(ConfigMetadata configMetadata, boolean isNewConfig) {
            super(new GridBagLayout());
            setBorder(JBUI.Borders.emptyRight(10));
            init(configMetadata, isNewConfig);
        }

        private void init(ConfigMetadata configMetadata, boolean isNewConfig) {

            // Config Title input title
            PropertyDescriptor configFileTitleDescriptor = PredefinedPropertyDescriptor.CONFIG_FILE_TITLE;
            PropertyTitleLabel configFileTitleLabel = new PropertyTitleLabel(configFileTitleDescriptor);

            StringInputField configTitleInputField = new StringInputField(configFileTitleDescriptor.getHintValue());
            configTitleInputField.setValue(configMetadata.getTitle());
            configTitleInputField.addListener(value -> configMetadata.setTitle((String) value));
            FormBuilder.get()
                    .addLabel(configFileTitleLabel, this)
                    .addLastField(configTitleInputField, this);

            // Config File Name input field
            PropertyDescriptor configFileNameDescriptor = PredefinedPropertyDescriptor.CONFIG_FILE_NAME;
            PropertyTitleLabel configFileNameLabel = new PropertyTitleLabel(configFileNameDescriptor);

            StringInputField configFileNameInputField = new StringInputField(configFileNameDescriptor.getHintValue());
            configFileNameInputField.setEnabled(isNewConfig);
            configFileNameInputField.setValue(configMetadata.getFileName());
            configFileNameInputField.addListener(value -> configMetadata.setFileName((String) value));
            FormBuilder.get()
                    .addLabel(configFileNameLabel, this)
                    .addLastField(configFileNameInputField, this);

            // Add Separator at the bottom
            FormBuilder.get().addLastField(new JSeparator(), this);
        }
    }
}

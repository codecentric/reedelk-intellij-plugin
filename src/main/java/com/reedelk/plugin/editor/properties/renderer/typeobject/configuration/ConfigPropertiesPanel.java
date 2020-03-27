package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

import com.intellij.openapi.module.Module;
import com.intellij.util.ui.JBUI;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.commons.InitValuesFiller;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.editor.properties.commons.PropertiesPanelHolder;
import com.reedelk.plugin.editor.properties.renderer.commons.StringInputField;
import com.reedelk.plugin.service.module.impl.configuration.ConfigMetadata;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.reedelk.plugin.message.ReedelkBundle.message;

class ConfigPropertiesPanel extends DisposablePanel {

    ConfigPropertiesPanel(Module module, ConfigMetadata configMetadata, TypeObjectDescriptor objectDescriptor, boolean isNewConfig) {

        List<PropertyDescriptor> descriptors = objectDescriptor.getObjectProperties();

        if (isNewConfig) {
            // Fill Default Properties Values
            InitValuesFiller.fill(configMetadata, descriptors);
        }

        ConfigMetadataHeaderPanel headerPanel = new ConfigMetadataHeaderPanel(configMetadata, isNewConfig);

        String componentFullyQualifiedName = objectDescriptor.getTypeFullyQualifiedName();

        PropertiesPanelHolder propertiesPanel =
                new PropertiesPanelHolder(module, componentFullyQualifiedName, configMetadata, descriptors);

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(headerPanel);
        add(propertiesPanel);
    }

    static class ConfigMetadataHeaderPanel extends DisposablePanel {

        ConfigMetadataHeaderPanel(ConfigMetadata configMetadata, boolean isNewConfig) {
            super(new GridBagLayout());
            setBorder(JBUI.Borders.emptyRight(10));
            init(configMetadata, isNewConfig);
        }

        private void init(ConfigMetadata configMetadata, boolean isNewConfig) {

            // Config File Name input field
            StringInputField configFileInputField = new StringInputField(message("config.field.file.hint"));
            configFileInputField.setEnabled(isNewConfig);
            configFileInputField.setValue(configMetadata.getFileName());
            configFileInputField.addListener(value -> configMetadata.setFileName((String) value));
            FormBuilder.get()
                    .addLabel(message("config.field.file"), this)
                    .addLastField(configFileInputField, this);

            // Config Title input title
            StringInputField configTitleInputField = new StringInputField(message("config.field.title.hint"));
            configTitleInputField.setValue(configMetadata.getTitle());
            configTitleInputField.addListener(value -> configMetadata.setTitle((String) value));
            FormBuilder.get()
                    .addLabel(message("config.field.title"), this)
                    .addLastField(configTitleInputField, this);

            // Add Separator at the bottom
            FormBuilder.get().addLastField(new JSeparator(), this);
        }
    }
}

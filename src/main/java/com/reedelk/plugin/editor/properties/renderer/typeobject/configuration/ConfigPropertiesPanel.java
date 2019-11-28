package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBPanel;
import com.intellij.util.ui.JBUI;
import com.reedelk.plugin.commons.DefaultDescriptorDataValuesFiller;
import com.reedelk.plugin.commons.Labels.Hint;
import com.reedelk.plugin.commons.Labels.PropertiesPanelConfig;
import com.reedelk.plugin.component.domain.ComponentDataHolder;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeDescriptor;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.editor.properties.commons.PropertiesPanelHolder;
import com.reedelk.plugin.editor.properties.renderer.PropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.PropertyTypeRendererFactory;
import com.reedelk.plugin.editor.properties.renderer.commons.StringInputField;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

class ConfigPropertiesPanel extends DisposablePanel {

    ConfigPropertiesPanel(Module module, ConfigMetadata configMetadata, TypeObjectDescriptor objectDescriptor, boolean isNewConfig) {

        List<ComponentPropertyDescriptor> descriptors = objectDescriptor.getObjectProperties();

        if (isNewConfig) {
            // Fill Default Properties Values
            DefaultDescriptorDataValuesFiller.fill(configMetadata, descriptors);
        }

        ConfigMetadataHeaderPanel headerPanel = new ConfigMetadataHeaderPanel(configMetadata, isNewConfig);

        ConfigPropertiesPanelHolder propertiesPanel = new ConfigPropertiesPanelHolder(configMetadata, descriptors);

        descriptors.forEach(propertyDescriptor -> {

            String propertyName = propertyDescriptor.getPropertyName();

            PropertyAccessor propertyAccessor = propertiesPanel.getAccessor(propertyName);

            TypeDescriptor propertyType = propertyDescriptor.getPropertyType();

            PropertyTypeRenderer renderer = PropertyTypeRendererFactory.get().from(propertyType);

            JComponent renderedComponent = renderer.render(module, propertyDescriptor, propertyAccessor, propertiesPanel);

            renderer.addToParent(propertiesPanel, renderedComponent, propertyDescriptor, propertiesPanel);

        });

        setLayout(new BorderLayout());
        add(headerPanel, NORTH);
        add(ContainerFactory.pushTop(propertiesPanel), CENTER);
    }

    class ConfigMetadataHeaderPanel extends JBPanel {

        ConfigMetadataHeaderPanel(ConfigMetadata configMetadata, boolean isNewConfig) {
            super(new GridBagLayout());
            setBorder(JBUI.Borders.emptyRight(10));
            init(configMetadata, isNewConfig);
        }

        private void init(ConfigMetadata configMetadata, boolean isNewConfig) {

            // Config File Name input field
            StringInputField configFileInputField = new StringInputField(Hint.CONFIG_FILE_TITLE);
            configFileInputField.setEnabled(isNewConfig);
            configFileInputField.setValue(configMetadata.getFileName());
            configFileInputField.addListener(value -> configMetadata.setFileName((String) value));
            FormBuilder.get()
                    .addLabel(PropertiesPanelConfig.FIELD_CONFIG_FILE, this)
                    .addLastField(configFileInputField, this);

            // Config Title input title
            StringInputField configTitleInputField = new StringInputField(Hint.CONFIG_TITLE);
            configTitleInputField.setValue(configMetadata.getTitle());
            configTitleInputField.addListener(value -> configMetadata.setTitle((String) value));
            FormBuilder.get()
                    .addLabel(PropertiesPanelConfig.FIELD_CONFIG_TITLE, this)
                    .addLastField(configTitleInputField, this);

            // Add Separator at the bottom
            FormBuilder.get().addLastField(new JSeparator(), this);
        }
    }

    class ConfigPropertiesPanelHolder extends PropertiesPanelHolder {

        ConfigPropertiesPanelHolder(ConfigMetadata configMetadata, List<ComponentPropertyDescriptor> descriptors) {
            super(configMetadata, descriptors);
        }

        /**
         * We override the default Properties panel holder because the accessors of data
         * displayed in this panel goes to the configuration file and not in the graph
         * component's data (snapshot).
         */
        protected PropertyAccessor getAccessor(String propertyName, TypeDescriptor propertyType, ComponentDataHolder dataHolder) {
            return PropertyAccessorFactory.get()
                    .typeDescriptor(propertyType)
                    .propertyName(propertyName)
                    .dataHolder(dataHolder)
                    .build();
        }
    }
}
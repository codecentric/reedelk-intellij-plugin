package com.esb.plugin.configuration.widget;

import com.esb.plugin.commons.DefaultDescriptorDataValuesFiller;
import com.esb.plugin.commons.Labels.PropertiesPanelConfig;
import com.esb.plugin.component.domain.ComponentDataHolder;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.esb.plugin.editor.properties.renderer.TypePropertyRenderer;
import com.esb.plugin.editor.properties.renderer.TypeRendererFactory;
import com.esb.plugin.editor.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.editor.properties.widget.FormBuilder;
import com.esb.plugin.editor.properties.widget.input.StringInputField;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

class ConfigPropertiesPanel extends JBPanel {

    private static final Dimension MINIMUM_PANEL_SIZE = new Dimension(400, 0);

    ConfigPropertiesPanel(Module module, ConfigMetadata configMetadata, TypeObjectDescriptor objectDescriptor, boolean isNewConfig) {

        List<ComponentPropertyDescriptor> descriptors = objectDescriptor.getObjectProperties();

        if (isNewConfig) {
            // Fill Default Properties Values
            DefaultDescriptorDataValuesFiller.fill(configMetadata, descriptors);
        }

        ConfigMetadataHeaderPanel headerPanel = new ConfigMetadataHeaderPanel(configMetadata, isNewConfig);

        DefaultConfigPropertiesPanel propertiesPanel = new DefaultConfigPropertiesPanel(configMetadata, descriptors);

        descriptors.forEach(propertyDescriptor -> {

            String displayName = propertyDescriptor.getDisplayName();

            String propertyName = propertyDescriptor.getPropertyName();

            PropertyAccessor propertyAccessor = propertiesPanel.getAccessor(propertyName);

            TypeDescriptor propertyType = propertyDescriptor.getPropertyType();

            TypePropertyRenderer renderer = TypeRendererFactory.get().from(propertyType);

            JComponent renderedComponent =
                    renderer.render(module, propertyDescriptor, propertyAccessor, propertiesPanel);

            renderer.addToParent(propertiesPanel, renderedComponent, displayName);
        });

        JPanel propertiesPanelWrapper = new JBPanel<>(new BorderLayout());
        propertiesPanelWrapper.add(propertiesPanel, NORTH);
        propertiesPanelWrapper.add(Box.createVerticalGlue(), CENTER);

        setLayout(new BorderLayout());
        add(headerPanel, NORTH);
        add(propertiesPanelWrapper, CENTER);
    }

    class ConfigMetadataHeaderPanel extends JBPanel {

        ConfigMetadataHeaderPanel(ConfigMetadata configMetadata, boolean isNewConfig) {
            super(new GridBagLayout());
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            init(configMetadata, isNewConfig);
        }

        private void init(ConfigMetadata configMetadata, boolean isNewConfig) {

            // Config File Name input field
            StringInputField configFileInputField = new StringInputField();
            configFileInputField.setEnabled(isNewConfig);
            configFileInputField.setValue(configMetadata.getFileName());
            configFileInputField.addListener(configMetadata::setFileName);
            FormBuilder.get()
                    .addLabel(PropertiesPanelConfig.FIELD_CONFIG_FILE, this)
                    .addLastField(configFileInputField, this);

            // Config Title input title
            StringInputField configTitleInputField = new StringInputField();
            configTitleInputField.setValue(configMetadata.getTitle());
            configTitleInputField.addListener(configMetadata::setTitle);
            FormBuilder.get()
                    .addLabel(PropertiesPanelConfig.FIELD_CONFIG_TITLE, this)
                    .addLastField(configTitleInputField, this);

            // Add Separator at the bottom
            FormBuilder.get()
                    .addLastField(new JSeparator(), this);
        }
    }

    class DefaultConfigPropertiesPanel extends DefaultPropertiesPanel {

        DefaultConfigPropertiesPanel(ConfigMetadata configMetadata, List<ComponentPropertyDescriptor> descriptors) {
            super(configMetadata, descriptors);
            setMinimumSize(MINIMUM_PANEL_SIZE);
        }

        // This one does not require a snapshot since it is not part of the flow data
        protected PropertyAccessor getAccessor(String propertyName, TypeDescriptor propertyType, ComponentDataHolder dataHolder) {
            return PropertyAccessorFactory.get()
                    .typeDescriptor(propertyType)
                    .propertyName(propertyName)
                    .dataHolder(dataHolder)
                    .build();
        }
    }
}

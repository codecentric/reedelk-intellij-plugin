package com.esb.plugin.configuration.widget;

import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.esb.plugin.editor.properties.renderer.type.TypeRendererFactory;
import com.esb.plugin.editor.properties.widget.DefaultPropertiesPanel;
import com.esb.plugin.editor.properties.widget.FormBuilder;
import com.esb.plugin.editor.properties.widget.input.StringInputField;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

class ConfigPropertiesPanel extends JBPanel {

    private boolean isNewConfig;

    ConfigPropertiesPanel(Module module, ConfigMetadata configMetadata, TypeObjectDescriptor descriptor, boolean isNewConfig) {
        this.isNewConfig = isNewConfig;

        ConfigMetadataHeaderPanel headerPanel = new ConfigMetadataHeaderPanel(configMetadata);

        DefaultPropertiesPanel propertiesPanel = new DefaultPropertiesPanel();

        descriptor.getObjectProperties().forEach(nestedPropertyDescriptor -> {

            final String displayName = nestedPropertyDescriptor.getDisplayName();
            final TypeDescriptor propertyType = nestedPropertyDescriptor.getPropertyType();

            // This one does not require a snapshot since it is not part of the flow data
            PropertyAccessor nestedPropertyAccessor = PropertyAccessorFactory.get()
                    .typeDescriptor(nestedPropertyDescriptor.getPropertyType())
                    .propertyName(nestedPropertyDescriptor.getPropertyName())
                    .dataHolder(configMetadata)
                    .build();

            TypeRendererFactory typeRendererFactory = TypeRendererFactory.get();
            JComponent renderedComponent = typeRendererFactory.from(propertyType)
                    .render(module, nestedPropertyDescriptor, nestedPropertyAccessor);

            FormBuilder.get()
                    .addLabel(displayName, propertiesPanel)
                    .addLastField(renderedComponent, propertiesPanel);
        });

        JPanel propertiesPanelWrapper = new JBPanel<>(new BorderLayout());
        propertiesPanelWrapper.add(propertiesPanel, NORTH);
        propertiesPanelWrapper.add(Box.createVerticalGlue(), CENTER);

        setLayout(new BorderLayout());
        add(headerPanel, NORTH);
        add(propertiesPanelWrapper, CENTER);
    }

    class ConfigMetadataHeaderPanel extends JBPanel {
        ConfigMetadataHeaderPanel(ConfigMetadata configMetadata) {
            super(new GridBagLayout());
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            init(configMetadata);
        }

        private void init(ConfigMetadata configMetadata) {

            // Config File Name input field
            StringInputField configFileInputField = new StringInputField();
            configFileInputField.setEnabled(isNewConfig);
            configFileInputField.setValue(configMetadata.getFileName());
            configFileInputField.addListener(configMetadata::setFileName);
            FormBuilder.get()
                    .addLabel("Config file", this)
                    .addLastField(configFileInputField, this);

            // Config Title input title
            StringInputField configTitleInputField = new StringInputField();
            configTitleInputField.setValue(configMetadata.getTitle());
            configTitleInputField.addListener(configMetadata::setTitle);
            FormBuilder.get()
                    .addLabel("Config title", this)
                    .addLastField(configTitleInputField, this);

            // Add Separator at the bottom
            FormBuilder.get()
                    .addLastField(new JSeparator(), this);
        }
    }
}

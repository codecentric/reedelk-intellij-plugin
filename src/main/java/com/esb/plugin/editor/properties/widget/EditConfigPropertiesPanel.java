package com.esb.plugin.editor.properties.widget;

import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.esb.plugin.editor.properties.renderer.type.TypeRendererFactory;
import com.esb.plugin.editor.properties.widget.input.StringInputField;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.openapi.module.Module;
import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.NORTH;

class EditConfigPropertiesPanel extends JBPanel {

    EditConfigPropertiesPanel(Module module, ConfigMetadata configMetadata, TypeObjectDescriptor descriptor) {

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
            StringInputField configFileInputField = new StringInputField();
            configFileInputField.setEnabled(false);
            configFileInputField.setValue(configMetadata.getFileName());
            FormBuilder.get()
                    .addLabel("Config file", this)
                    .addLastField(configFileInputField, this);

            StringInputField configNameInputField = new StringInputField();
            configNameInputField.setValue(configMetadata.getTitle());
            FormBuilder.get()
                    .addLabel("Config title", this)
                    .addLastField(configNameInputField, this);

            FormBuilder.get()
                    .addLastField(new JSeparator(), this);
        }
    }
}

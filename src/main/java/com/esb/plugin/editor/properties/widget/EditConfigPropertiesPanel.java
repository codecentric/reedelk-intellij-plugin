package com.esb.plugin.editor.properties.widget;

import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
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
import java.util.List;

public class EditConfigPropertiesPanel extends JBPanel {

    public EditConfigPropertiesPanel(Module module, ConfigMetadata configMetadata, TypeObjectDescriptor descriptor) {

        ConfigMetadataHeaderPanel headerPanel = new ConfigMetadataHeaderPanel();

        StringInputField configFileInputField = new StringInputField();
        configFileInputField.setEnabled(false);
        configFileInputField.setValue(configMetadata.getFileName());
        FormBuilder.get()
                .addLabel("Config file", headerPanel)
                .addLastField(configFileInputField, headerPanel);

        StringInputField configNameInputField = new StringInputField();
        configNameInputField.setValue(configMetadata.getTitle());
        FormBuilder.get()
                .addLabel("Config title", headerPanel)
                .addLastField(configNameInputField, headerPanel);

        FormBuilder.get()
                .addLastField(new JSeparator(), headerPanel);


        // The panel should contain at the top the file name
        List<ComponentPropertyDescriptor> objectProperties = descriptor.getObjectProperties();


        DefaultPropertiesPanel propertiesPanel = new DefaultPropertiesPanel();
        objectProperties.forEach(nestedPropertyDescriptor -> {

            final String displayName = nestedPropertyDescriptor.getDisplayName();
            final TypeDescriptor propertyType = nestedPropertyDescriptor.getPropertyType();

            // The accessor of type object returns a TypeObject map.


            // This one does not require a snapshot since it is not part of the flow data
            PropertyAccessor nestedPropertyAccessor = PropertyAccessorFactory.get()
                    .typeDescriptor(nestedPropertyDescriptor.getPropertyType())
                    .dataHolder(configMetadata)
                    .propertyName(nestedPropertyDescriptor.getPropertyName())
                    .build();

            TypeRendererFactory typeRendererFactory = TypeRendererFactory.get();
            JComponent renderedComponent = typeRendererFactory.from(propertyType)
                    .render(module, nestedPropertyDescriptor, nestedPropertyAccessor);

            FormBuilder.get()
                    .addLabel(displayName, propertiesPanel)
                    .addLastField(renderedComponent, propertiesPanel);
        });

        JPanel propertiesPanelWrapper = new JBPanel<>(new BorderLayout());
        propertiesPanelWrapper.add(propertiesPanel, BorderLayout.NORTH);
        propertiesPanelWrapper.add(Box.createVerticalGlue(), BorderLayout.CENTER);

        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(propertiesPanelWrapper, BorderLayout.CENTER);
    }

    class ConfigMetadataHeaderPanel extends JBPanel {
        ConfigMetadataHeaderPanel() {
            super(new GridBagLayout());
            setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        }
    }
}

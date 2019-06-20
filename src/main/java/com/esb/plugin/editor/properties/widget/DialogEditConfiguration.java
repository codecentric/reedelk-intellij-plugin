package com.esb.plugin.editor.properties.widget;

import com.esb.plugin.commons.Labels;
import com.esb.plugin.component.domain.ComponentDataHolder;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeDescriptor;
import com.esb.plugin.component.domain.TypeObjectDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessorFactory;
import com.esb.plugin.editor.properties.renderer.type.TypeRendererFactory;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public class DialogEditConfiguration extends DialogWrapper {

    private final TypeObjectDescriptor objectDescriptor;
    private final Module module;
    private ConfigMetadata selectedMetadata;

    public DialogEditConfiguration(@NotNull Module module, @NotNull TypeObjectDescriptor typeObjectDescriptor, @NotNull ConfigMetadata selectedConfig) {
        super(module.getProject(), false);
        this.objectDescriptor = typeObjectDescriptor;
        this.module = module;
        this.selectedMetadata = selectedConfig;
        setTitle(Labels.DIALOG_TITLE_EDIT_CONFIGURATION);
        init();
    }

    @NotNull
    @Override
    protected Action getOKAction() {
        Action okAction = super.getOKAction();
        okAction.putValue(Action.NAME, Labels.DIALOG_BTN_SAVE_CONFIGURATION);
        return okAction;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        // TODO: We need to create the panel here and populate it with
        // TODO: the selection
        List<ComponentPropertyDescriptor> objectProperties = objectDescriptor.getObjectProperties();

        DefaultPropertiesPanel propertiesPanel = new DefaultPropertiesPanel();
        objectProperties.forEach(nestedPropertyDescriptor -> {

            final String displayName = nestedPropertyDescriptor.getDisplayName();
            final TypeDescriptor propertyType = nestedPropertyDescriptor.getPropertyType();

            // The accessor of type object returns a TypeObject map.
            ComponentDataHolder dataHolder = selectedMetadata;

            // This one does not require a snapshot since it is not part of the flow data
            PropertyAccessor nestedPropertyAccessor = PropertyAccessorFactory.get()
                    .typeDescriptor(nestedPropertyDescriptor.getPropertyType())
                    .dataHolder(dataHolder)
                    .propertyName(nestedPropertyDescriptor.getPropertyName())
                    .build();

            TypeRendererFactory typeRendererFactory = TypeRendererFactory.get();
            JComponent renderedComponent = typeRendererFactory.from(propertyType)
                    .render(module, nestedPropertyDescriptor, nestedPropertyAccessor);

            FormBuilder.get()
                    .addLabel(displayName, propertiesPanel)
                    .addLastField(renderedComponent, propertiesPanel);
        });
        return propertiesPanel;
    }

    public void save() {
        // Save selected Metadata into its own file
    }
}

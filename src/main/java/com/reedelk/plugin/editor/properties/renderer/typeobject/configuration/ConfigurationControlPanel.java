package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.plugin.commons.TypeObjectFactory;
import com.reedelk.plugin.editor.properties.commons.ComboActionsPanel;
import com.reedelk.plugin.editor.properties.commons.DialogConfirmAction;
import com.reedelk.plugin.service.module.ConfigurationService;
import com.reedelk.plugin.service.module.impl.configuration.ConfigMetadata;
import com.reedelk.plugin.service.module.impl.configuration.NewConfigMetadata;

import java.util.Optional;
import java.util.UUID;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.commons.JsonParser.Config;
import static com.reedelk.runtime.commons.JsonParser.Implementor;

public class ConfigurationControlPanel extends ComboActionsPanel<ConfigMetadata> {

    private final String dialogTitle;
    private final transient Module module;
    private final transient ObjectDescriptor typeDescriptor;

    public ConfigurationControlPanel(Module module, ObjectDescriptor typeDescriptor) {
        super();
        this.module = module;
        this.typeDescriptor = typeDescriptor;
        this.dialogTitle = Optional.ofNullable(typeDescriptor.getDialogTitle())
                .orElse("Configuration");
    }

    @Override
    protected void onSelect(ConfigMetadata newSelected) {
        enableEdit(newSelected.isEditable());
        enableDelete(newSelected.isEditable());
    }

    @Override
    protected void onAdd(ConfigMetadata item) {
        // We ignore the selected. Create new config object.
        ObjectDescriptor.TypeObject configTypeObject = TypeObjectFactory.newInstance();
        configTypeObject.set(Implementor.name(), typeDescriptor.getTypeFullyQualifiedName());
        configTypeObject.set(Config.id(), UUID.randomUUID().toString());
        configTypeObject.set(Config.title(), message("config.field.title.default"));

        ConfigMetadata newConfigMetadata = new NewConfigMetadata(message("config.field.file.default"), configTypeObject, typeDescriptor);

        DialogConfiguration dialogAddConfiguration = DialogConfiguration.builder()
                .isNewConfig()
                .module(module)
                .objectDescriptor(typeDescriptor)
                .configMetadata(newConfigMetadata)
                .title(message("config.dialog.add.title", dialogTitle))
                .okActionLabel(message("config.dialog.add.btn.add"))
                .build();

        if (dialogAddConfiguration.showAndGet()) {
            ConfigurationService.getInstance(module).addConfiguration(newConfigMetadata);
        }
    }

    @Override
    protected void onEdit(ConfigMetadata item) {
        if (item.isEditable()) {
            DialogConfiguration dialogEditConfiguration = DialogConfiguration.builder()
                    .module(module)
                    .configMetadata(item)
                    .objectDescriptor(typeDescriptor)
                    .title(message("config.dialog.edit.title", dialogTitle))
                    .okActionLabel(message("config.dialog.edit.btn.edit"))
                    .build();
            if (dialogEditConfiguration.showAndGet()) {
                ConfigurationService.getInstance(module).saveConfiguration(item);
            }
        }
    }

    @Override
    protected void onDelete(ConfigMetadata item) {
        if (item.isRemovable()) {
            DialogConfirmAction dialogConfirmDelete =
                    new DialogConfirmAction(module,
                            message("config.dialog.delete.title", dialogTitle),
                            message("config.dialog.delete.confirm.message"));
            if (dialogConfirmDelete.showAndGet()) {
                ConfigurationService.getInstance(module).removeConfiguration(item);
            }
        }
    }
}

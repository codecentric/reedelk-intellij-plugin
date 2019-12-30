package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.commons.TypeObjectFactory;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.DialogConfirmAction;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.service.module.ConfigService;
import com.reedelk.plugin.service.module.impl.configuration.ConfigMetadata;
import com.reedelk.plugin.service.module.impl.configuration.NewConfigMetadata;

import java.util.UUID;

import static com.intellij.icons.AllIcons.Actions.EditSource;
import static com.intellij.icons.AllIcons.General.Add;
import static com.intellij.icons.AllIcons.General.Remove;
import static com.reedelk.module.descriptor.model.TypeObjectDescriptor.TypeObject;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static com.reedelk.runtime.commons.JsonParser.Config;
import static com.reedelk.runtime.commons.JsonParser.Implementor;

public class ConfigControlPanel extends DisposablePanel {

    private final transient Module module;
    private final transient ClickableLabel editAction;
    private final transient ClickableLabel deleteAction;
    private final transient TypeObjectDescriptor typeDescriptor;

    private transient ConfigMetadata selected;

    public ConfigControlPanel(Module module, TypeObjectDescriptor typeDescriptor) {
        this.module = module;
        this.typeDescriptor = typeDescriptor;
        deleteAction = new ClickableLabel(message("config.actions.btn.delete"), Remove, Remove, this::deleteConfiguration);
        editAction = new ClickableLabel(message("config.actions.btn.edit"), EditSource, EditSource, this::editConfiguration);
        ClickableLabel addAction = new ClickableLabel(message("config.actions.btn.add"), Add, Add, this::addConfiguration);
        add(editAction);
        add(addAction);
        add(deleteAction);
    }

    public void onSelect(ConfigMetadata newSelected) {
        this.selected = newSelected;
        this.editAction.setEnabled(newSelected.isEditable());
        this.deleteAction.setEnabled(newSelected.isRemovable());
    }

    private void addConfiguration() {
        // We ignore the selected. Create new config object.
        TypeObject configTypeObject = TypeObjectFactory.newInstance();
        configTypeObject.set(Implementor.name(), typeDescriptor.getTypeFullyQualifiedName());
        configTypeObject.set(Config.id(), UUID.randomUUID().toString());
        configTypeObject.set(Config.title(), message("config.field.title.default"));

        ConfigMetadata newConfigMetadata = new NewConfigMetadata(message("config.field.file.default"), configTypeObject, typeDescriptor);

        ConfigurationDialog dialogAddConfiguration = ConfigurationDialog.builder()
                .isNewConfig()
                .module(module)
                .objectDescriptor(typeDescriptor)
                .configMetadata(newConfigMetadata)
                .title(message("config.dialog.add.title"))
                .okActionLabel(message("config.dialog.add.btn.add"))
                .build();

        if (dialogAddConfiguration.showAndGet()) {
            ConfigService.getInstance(module).addConfig(newConfigMetadata);
        }
    }

    private void editConfiguration() {
        if (selected.isEditable()) {
            ConfigurationDialog dialogEditConfiguration = ConfigurationDialog.builder()
                    .module(module)
                    .configMetadata(selected)
                    .objectDescriptor(typeDescriptor)
                    .title(message("config.dialog.edit.title"))
                    .okActionLabel(message("config.dialog.edit.btn.edit"))
                    .build();
            if (dialogEditConfiguration.showAndGet()) {
                ConfigService.getInstance(module).saveConfig(selected);
            }
        }
    }

    private void deleteConfiguration() {
        if (selected.isRemovable()) {
            DialogConfirmAction dialogConfirmDelete =
                    new DialogConfirmAction(module,
                            message("config.dialog.delete.title"),
                            message("config.dialog.delete.confirm.message"));
            if (dialogConfirmDelete.showAndGet()) {
                ConfigService.getInstance(module).removeConfig(selected);
            }
        }
    }
}

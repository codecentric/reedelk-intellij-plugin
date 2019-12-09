package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.DialogConfirmAction;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.service.module.ConfigService;
import com.reedelk.plugin.service.module.impl.configuration.ConfigMetadata;
import com.reedelk.plugin.service.module.impl.configuration.NewConfigMetadata;
import com.reedelk.runtime.commons.JsonParser;

import java.util.UUID;

import static com.intellij.icons.AllIcons.Actions.EditSource;
import static com.intellij.icons.AllIcons.General.Add;
import static com.intellij.icons.AllIcons.General.Remove;
import static com.reedelk.plugin.component.domain.TypeObjectDescriptor.TypeObject;
import static com.reedelk.plugin.message.ReedelkBundle.message;

public class ConfigControlPanel extends DisposablePanel {

    private transient final Module module;
    private transient ConfigMetadata selected;
    private transient final ClickableLabel editAction;
    private transient final ClickableLabel deleteAction;
    private transient final TypeObjectDescriptor typeDescriptor;

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
        TypeObject configTypeObject = new TypeObject(typeDescriptor.getTypeFullyQualifiedName());
        configTypeObject.set(JsonParser.Config.id(), UUID.randomUUID().toString());
        configTypeObject.set(JsonParser.Config.title(), message("config.field.title.default"));

        ConfigMetadata newConfig = new NewConfigMetadata(message("config.field.file.default"), configTypeObject, typeDescriptor);
        DialogAddConfiguration dialogAddConfiguration = new DialogAddConfiguration(module, typeDescriptor, newConfig);

        if (dialogAddConfiguration.showAndGet()) {
            ConfigService.getInstance(module).addConfig(newConfig);
        }
    }

    private void editConfiguration() {
        if (selected.isEditable()) {
            DialogEditConfiguration dialogEditConfiguration = new DialogEditConfiguration(module, typeDescriptor, selected);
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

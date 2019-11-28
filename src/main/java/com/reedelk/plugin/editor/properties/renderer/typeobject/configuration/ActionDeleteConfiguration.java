package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.Labels;
import com.reedelk.plugin.editor.properties.commons.DialogConfirmAction;
import com.reedelk.plugin.service.module.ConfigService;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;
import org.jetbrains.annotations.NotNull;

import static com.reedelk.plugin.commons.Icons.Config.Delete;
import static com.reedelk.plugin.commons.Icons.Config.DeleteDisabled;

public class ActionDeleteConfiguration extends ActionableCommandButton {

    private final Module module;
    private DeleteCompleteListener listener;

    ActionDeleteConfiguration(@NotNull Module module) {
        super("Delete", Delete, DeleteDisabled);
        this.module = module;
    }

    @Override
    protected void onClick(@NotNull ConfigMetadata selectedMetadata) {
        if (!selectedMetadata.isRemovable()) return;

        DialogConfirmAction dialogConfirmDelete = new DialogConfirmAction(
                module,
                Labels.DIALOG_TITLE_DELETE_CONFIGURATION,
                Labels.DIALOG_MESSAGE_DELETE_CONFIRM);
        if (dialogConfirmDelete.showAndGet()) {
            try {
                ConfigService.getInstance(module).removeConfig(selectedMetadata);
                if (listener != null) {
                    listener.onDeletedConfiguration(selectedMetadata);
                }
            } catch (Exception exception) {
                if (listener != null) {
                    listener.onDeletedConfigurationError(exception, selectedMetadata);
                }
            }
        }
    }

    @Override
    public void onSelect(ConfigMetadata configMetadata) {
        super.onSelect(configMetadata);
        setEnabled(configMetadata.isRemovable());
    }

    public void addListener(DeleteCompleteListener listener) {
        this.listener = listener;
    }

    public interface DeleteCompleteListener {
        void onDeletedConfiguration(ConfigMetadata deletedConfig);

        void onDeletedConfigurationError(Exception exception, ConfigMetadata configMetadata);
    }
}

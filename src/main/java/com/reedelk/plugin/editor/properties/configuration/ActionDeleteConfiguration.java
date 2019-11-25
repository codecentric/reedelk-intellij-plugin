package com.reedelk.plugin.editor.properties.configuration;

import com.intellij.openapi.module.Module;
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

        DialogRemoveConfiguration dialogRemoveConfiguration = new DialogRemoveConfiguration(module);
        if (dialogRemoveConfiguration.showAndGet()) {
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

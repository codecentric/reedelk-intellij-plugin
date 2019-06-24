package com.esb.plugin.configuration.widget;

import com.esb.plugin.service.module.ConfigService;
import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import static com.esb.plugin.commons.Icons.Config.Delete;
import static com.esb.plugin.commons.Icons.Config.DeleteDisabled;

public class ActionDeleteConfiguration extends ActionableCommandButton {

    private final Module module;
    private DeleteCompleteListener listener;

    public ActionDeleteConfiguration(@NotNull Module module) {
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

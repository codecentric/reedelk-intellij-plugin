package com.esb.plugin.configuration.widget;

import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import static com.esb.plugin.commons.Icons.Config.Delete;

public class ActionDeleteConfiguration extends ActionableCommandButton {

    private final Module module;
    private DeleteCompleteListener listener;

    public ActionDeleteConfiguration(@NotNull Module module) {
        super("Delete", Delete);
        this.module = module;
    }

    @Override
    protected void onClick(@NotNull ConfigMetadata selectedMetadata) {

        DialogRemoveConfiguration dialogRemoveConfiguration = new DialogRemoveConfiguration(module, selectedMetadata);

        if (dialogRemoveConfiguration.showAndGet()) {

            dialogRemoveConfiguration.delete();

            if (listener != null) {
                listener.onDeletedConfiguration(selectedMetadata);
            }

        }
    }

    public void addListener(DeleteCompleteListener listener) {
        this.listener = listener;
    }

    public interface DeleteCompleteListener {
        void onDeletedConfiguration(ConfigMetadata deletedConfig);
    }
}

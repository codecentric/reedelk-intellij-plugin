package com.esb.plugin.configuration.widget;

import com.esb.plugin.service.module.impl.ConfigMetadata;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import static com.esb.plugin.commons.Icons.Config.Delete;

public class ActionDeleteConfiguration extends ActionableCommandButton {

    private final Module module;

    public ActionDeleteConfiguration(@NotNull Module module) {
        super("Delete", Delete);
        this.module = module;
    }

    @Override
    protected void onClick(@NotNull ConfigMetadata selectedMetadata) {

        DialogRemoveConfiguration dialogRemoveConfiguration = new DialogRemoveConfiguration(module);

        if (dialogRemoveConfiguration.showAndGet()) {

            dialogRemoveConfiguration.delete();

        }
    }
}

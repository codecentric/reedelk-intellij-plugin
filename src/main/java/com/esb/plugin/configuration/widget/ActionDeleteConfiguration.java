package com.esb.plugin.configuration.widget;

import com.intellij.openapi.module.Module;

import static com.esb.plugin.commons.Icons.Config.Delete;

public class ActionDeleteConfiguration extends ActionableCommandButton {

    public ActionDeleteConfiguration(Module module) {
        super("Delete", Delete);
        addListener(selectedConfig -> {
            DialogRemoveConfiguration dialogRemoveConfiguration = new DialogRemoveConfiguration(module);
            if (dialogRemoveConfiguration.showAndGet()) {
                dialogRemoveConfiguration.delete();
            }
        });
    }
}

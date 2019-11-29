package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.DialogConfirmAction;
import com.reedelk.plugin.service.module.ConfigService;
import com.reedelk.plugin.service.module.impl.ConfigMetadata;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseEvent;

import static com.reedelk.plugin.commons.Icons.Config.Delete;
import static com.reedelk.plugin.commons.Icons.Config.DeleteDisabled;
import static com.reedelk.plugin.message.ReedelkBundle.message;

public class ActionDeleteConfiguration extends ClickableLabel {

    private final Module module;
    private ConfigMetadata selected;

    ActionDeleteConfiguration(@NotNull Module module) {
        super("Delete", Delete, DeleteDisabled);
        this.module = module;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
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

    void onSelect(ConfigMetadata selected) {
        this.selected = selected;
        setEnabled(selected.isRemovable());
    }
}

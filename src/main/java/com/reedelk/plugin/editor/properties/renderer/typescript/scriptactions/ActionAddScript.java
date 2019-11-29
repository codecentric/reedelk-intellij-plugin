package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.service.module.ScriptService;
import org.jetbrains.annotations.NotNull;

import java.awt.event.MouseEvent;

import static com.reedelk.plugin.commons.Icons.Config.Add;

class ActionAddScript extends ClickableLabel {

    private final Module module;

    ActionAddScript(@NotNull Module module) {
        super("Add", Add, Add);
        this.module = module;
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        DialogAddScript dialogAddScript = new DialogAddScript(module.getProject());
        if (dialogAddScript.showAndGet()) {
            ScriptService.getInstance(module).addScript(dialogAddScript.getScriptFileNameWithPathToAdd());
        }
    }
}

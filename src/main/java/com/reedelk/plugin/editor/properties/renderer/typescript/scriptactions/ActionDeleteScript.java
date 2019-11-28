package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.renderer.typescript.ScriptPropertyRenderer;
import com.reedelk.plugin.service.module.ScriptService;
import com.reedelk.plugin.service.module.impl.ScriptResource;

import java.awt.event.MouseEvent;

import static com.reedelk.plugin.commons.Icons.Config.Delete;
import static com.reedelk.plugin.commons.Icons.Config.DeleteDisabled;
import static com.reedelk.plugin.editor.properties.renderer.typescript.ScriptSelectorCombo.UNSELECTED;

class ActionDeleteScript extends ClickableLabel {

    private final Module module;
    private ScriptResource selected;

    ActionDeleteScript(Module module) {
        super("Delete", Delete, DeleteDisabled);
        this.module = module;
    }


    // TODO: Add confirm dialog
    @Override
    public void mouseClicked(MouseEvent event) {
        ScriptService.getInstance(module).removeScript(selected.getPath());
    }

    public void onSelect(ScriptResource value) {
        if (value instanceof ScriptPropertyRenderer.NotFoundScriptResource || value == UNSELECTED) {
            // We disable the button if it the script resource is not found or not selected.
            setEnabled(false);
        } else {
            setEnabled(true);
        }
        this.selected = value;
    }
}

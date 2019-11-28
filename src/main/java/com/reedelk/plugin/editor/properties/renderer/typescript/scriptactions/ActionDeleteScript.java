package com.reedelk.plugin.editor.properties.renderer.typescript.scriptactions;

import com.reedelk.plugin.editor.properties.commons.ClickableLabel;

import static com.reedelk.plugin.commons.Icons.Config.Delete;
import static com.reedelk.plugin.commons.Icons.Config.DeleteDisabled;

class ActionDeleteScript extends ClickableLabel {

    ActionDeleteScript() {
        super("Delete", Delete, DeleteDisabled);
    }
}

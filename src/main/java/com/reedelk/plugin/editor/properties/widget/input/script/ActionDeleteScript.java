package com.reedelk.plugin.editor.properties.widget.input.script;

import com.reedelk.plugin.editor.properties.widget.ClickableLabel;

import static com.reedelk.plugin.commons.Icons.Config.Delete;
import static com.reedelk.plugin.commons.Icons.Config.DeleteDisabled;

class ActionDeleteScript extends ClickableLabel {

    ActionDeleteScript() {
        super("Delete", Delete, DeleteDisabled);
    }
}

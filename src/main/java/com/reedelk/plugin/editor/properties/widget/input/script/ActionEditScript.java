package com.reedelk.plugin.editor.properties.widget.input.script;

import com.reedelk.plugin.editor.properties.widget.ClickableLabel;

import static com.reedelk.plugin.commons.Icons.Config.Edit;
import static com.reedelk.plugin.commons.Icons.Config.EditDisabled;

class ActionEditScript extends ClickableLabel {

    ActionEditScript() {
        super("Edit", Edit, EditDisabled);
    }
}

package com.reedelk.plugin.editor.properties.widget.input.script;

import com.reedelk.plugin.editor.properties.widget.DisposablePanel;

public class ScriptControlPanel extends DisposablePanel {

    private final ActionDeleteScript deleteAction;
    private final ActionEditScript editAction;
    private final ActionAddScript addAction;

    public ScriptControlPanel() {
        deleteAction = new ActionDeleteScript();
        editAction = new ActionEditScript();
        addAction = new ActionAddScript();
        add(editAction);
        add(deleteAction);
        add(addAction);
    }
}

package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class EditCustomObjectDialog extends DialogWrapper implements Disposable {

    protected EditCustomObjectDialog(@NotNull Module module, String title) {
        super(module.getProject(), true);
        setTitle(title);
        init();
    }

    @NotNull
    @Override
    protected Action getOKAction() {
        Action okAction = super.getOKAction();
        okAction.putValue(Action.NAME, "Done");
        return okAction;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JLabel done = new JLabel("Super");
        JPanel content = new JPanel();
        content.add(done);
        setCrossClosesWindow(true);
        return content;
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}

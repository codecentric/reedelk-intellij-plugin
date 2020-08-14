package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import com.reedelk.plugin.commons.DisposableUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;

/**
 * Dialog used by Configuration properties and Custom Object Map value.
 */
public abstract class DialogAbstractProperties extends DialogWrapper implements Disposable {

    private static final int MINIMUM_PANEL_WIDTH = 560;
    private static final int MINIMUM_PANEL_HEIGHT = 0;

    private DisposableScrollPane panel;
    private String okActionLabel;

    protected DialogAbstractProperties(Module module, String title, String okActionLabel) {
        super(module.getProject(), false);
        this.okActionLabel = okActionLabel;
        setTitle(title);
        setResizable(true);
        setAutoAdjustable(true);
        setCrossClosesWindow(true);
    }

    @NotNull
    @Override
    protected Action getOKAction() {
        Action okAction = super.getOKAction();
        okAction.putValue(Action.NAME, okActionLabel);
        return okAction;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        JComponent content = content();
        DisposablePanelFixedWidth decorator = new DisposablePanelFixedWidth(content, MINIMUM_PANEL_WIDTH);

        panel = ContainerFactory.makeItScrollable(ContainerFactory.pushTop(decorator));
        panel.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        panel.setMinimumSize(new Dimension(MINIMUM_PANEL_WIDTH, MINIMUM_PANEL_HEIGHT));
        return panel;
    }

    @Override
    public void dispose() {
        super.dispose();
        DisposableUtils.dispose(panel);
    }

    protected abstract JComponent content();

}

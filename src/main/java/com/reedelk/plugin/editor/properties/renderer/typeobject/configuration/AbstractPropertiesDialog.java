package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.DisposableScrollPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;

/**
 * Dialog used by Configuration properties and Custom Object Map value.
 */
public abstract class AbstractPropertiesDialog extends DialogWrapper implements Disposable {

    private static final int MINIMUM_PANEL_WIDTH = 550;
    private static final int MINIMUM_PANEL_HEIGHT = 0;

    private DisposableScrollPane panel;
    private String okActionLabel;

    protected AbstractPropertiesDialog(Module module, String title, String okActionLabel) {
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
        FixedWidthConfigPropertiesPanel decorator = new FixedWidthConfigPropertiesPanel(content);

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


    private static class FixedWidthConfigPropertiesPanel extends DisposablePanel {

        FixedWidthConfigPropertiesPanel(JComponent decorated) {
            setLayout(new BorderLayout());
            add(decorated, BorderLayout.CENTER);
        }

        @Override
        public Dimension getPreferredSize() {
            Dimension preferredSize = super.getPreferredSize();
            if (preferredSize.width > MINIMUM_PANEL_WIDTH) {
                preferredSize.width = MINIMUM_PANEL_WIDTH;
            }
            return preferredSize;
        }
    }
}

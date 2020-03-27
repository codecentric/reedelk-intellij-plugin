package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.commons.DisposableScrollPane;
import com.reedelk.plugin.service.module.impl.configuration.ConfigMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class ConfigurationDialog extends DialogWrapper implements Disposable {

    private static final int MINIMUM_PANEL_WIDTH = 500;
    private static final int MINIMUM_PANEL_HEIGHT = 0;

    private Module module;
    private DisposableScrollPane panel;
    private ConfigMetadata configMetadata;
    private TypeObjectDescriptor objectDescriptor;

    private boolean isNewConfig;
    private String okActionLabel;

    private ConfigurationDialog(Module module, String title) {
        super(module.getProject(), false);
        this.module = module;
        setTitle(title);
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
        DisposablePanel propertiesPanel = new ConfigPropertiesPanel(module, configMetadata, objectDescriptor, isNewConfig);
        this.panel = ContainerFactory.makeItScrollable(propertiesPanel);
        this.panel.setMinimumSize(new Dimension(MINIMUM_PANEL_WIDTH, MINIMUM_PANEL_HEIGHT));
        setCrossClosesWindow(true);
        return this.panel;
    }

    @Override
    public void dispose() {
        super.dispose();
        if (panel != null) {
            panel.dispose();
            panel = null;
        }
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {

        private Module module;
        private ConfigMetadata configMetadata;
        private TypeObjectDescriptor objectDescriptor;
        private String title;
        private String okActionLabel;
        private boolean isNewConfig;

        Builder isNewConfig() {
            this.isNewConfig = true;
            return this;
        }

        Builder title(String title) {
            this.title = title;
            return this;
        }

        Builder module(Module module) {
            this.module = module;
            return this;
        }

        Builder okActionLabel(String label) {
            this.okActionLabel = label;
            return this;
        }

        Builder configMetadata(ConfigMetadata configMetadata) {
            this.configMetadata = configMetadata;
            return this;
        }

        Builder objectDescriptor(TypeObjectDescriptor objectDescriptor) {
            this.objectDescriptor = objectDescriptor;
            return this;
        }

        ConfigurationDialog build() {
            ConfigurationDialog dialog = new ConfigurationDialog(module, title);
            dialog.isNewConfig = isNewConfig;
            dialog.okActionLabel = okActionLabel;
            dialog.configMetadata = configMetadata;
            dialog.objectDescriptor = objectDescriptor;
            dialog.init();
            return dialog;
        }
    }
}

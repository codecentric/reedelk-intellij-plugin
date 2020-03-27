package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.commons.DisposableUtils;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.commons.DisposableScrollPane;
import com.reedelk.plugin.service.module.impl.configuration.ConfigMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

import static javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER;

public class ConfigurationDialog extends DialogWrapper implements Disposable {

    private static final int MINIMUM_PANEL_WIDTH = 550;
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
        FixedWidthConfigPropertiesPanel propertiesPanel = new FixedWidthConfigPropertiesPanel(module, configMetadata, objectDescriptor, isNewConfig);
        panel = ContainerFactory.makeItScrollable(ContainerFactory.pushTop(propertiesPanel));
        panel.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        panel.setMinimumSize(new Dimension(MINIMUM_PANEL_WIDTH, MINIMUM_PANEL_HEIGHT));
        return panel;
    }

    @Override
    public void dispose() {
        super.dispose();
        DisposableUtils.dispose(panel);
    }

    static class FixedWidthConfigPropertiesPanel extends ConfigPropertiesPanel {

        FixedWidthConfigPropertiesPanel(Module module, ConfigMetadata configMetadata, TypeObjectDescriptor objectDescriptor, boolean isNewConfig) {
            super(module, configMetadata, objectDescriptor, isNewConfig);
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

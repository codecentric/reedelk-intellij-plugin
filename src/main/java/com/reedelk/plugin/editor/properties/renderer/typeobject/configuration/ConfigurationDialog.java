package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.service.module.impl.configuration.ConfigMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ConfigurationDialog extends DialogWrapper {

    private Module module;
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
        ConfigPropertiesPanel panel = new ConfigPropertiesPanel(module, configMetadata, objectDescriptor, isNewConfig);
        return ContainerFactory.makeItScrollable(panel);
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
            dialog.configMetadata = configMetadata;
            dialog.objectDescriptor = objectDescriptor;
            dialog.okActionLabel = okActionLabel;
            dialog.init();
            return dialog;
        }
    }
}

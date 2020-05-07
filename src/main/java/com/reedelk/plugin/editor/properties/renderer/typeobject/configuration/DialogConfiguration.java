package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.plugin.editor.properties.commons.DialogAbstractProperties;
import com.reedelk.plugin.service.module.impl.configuration.ConfigMetadata;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DialogConfiguration extends DialogAbstractProperties {

    private Module module;
    private ConfigMetadata configMetadata;
    private ObjectDescriptor objectDescriptor;

    private boolean isNewConfig;

    private DialogConfiguration(@NotNull Module module,
                                @NotNull String title,
                                @NotNull String okActionLabel) {
        super(module, title, okActionLabel);
        this.module = module;
    }

    @Override
    protected JComponent content() {
        return new ConfigurationPropertiesPanel(module, configMetadata, objectDescriptor, isNewConfig);
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{getOKAction()};
    }

    static Builder builder() {
        return new Builder();
    }

    static class Builder {

        private Module module;
        private ConfigMetadata configMetadata;
        private ObjectDescriptor objectDescriptor;
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

        Builder objectDescriptor(ObjectDescriptor objectDescriptor) {
            this.objectDescriptor = objectDescriptor;
            return this;
        }

        DialogConfiguration build() {
            DialogConfiguration dialog = new DialogConfiguration(module, title, okActionLabel);
            dialog.isNewConfig = isNewConfig;
            dialog.configMetadata = configMetadata;
            dialog.objectDescriptor = objectDescriptor;
            dialog.init();
            return dialog;
        }
    }
}

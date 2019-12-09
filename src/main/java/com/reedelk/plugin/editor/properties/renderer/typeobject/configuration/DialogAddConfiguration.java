package com.reedelk.plugin.editor.properties.renderer.typeobject.configuration;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.DialogWrapper;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.service.module.impl.configuration.ConfigMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class DialogAddConfiguration extends DialogWrapper {

    private static final boolean NEW_CONFIG = true;

    private final TypeObjectDescriptor objectDescriptor;
    private final ConfigMetadata newConfigMetadata;
    private final Module module;

    DialogAddConfiguration(@NotNull Module module,
                           @NotNull TypeObjectDescriptor objectDescriptor,
                           @NotNull ConfigMetadata newConfig) {
        super(module.getProject(), false);
        this.objectDescriptor = objectDescriptor;
        this.newConfigMetadata = newConfig;
        this.module = module;

        setTitle(message("config.dialog.add.title"));
        init();
    }

    @NotNull
    @Override
    protected Action getOKAction() {
        Action okAction = super.getOKAction();
        okAction.putValue(Action.NAME, message("config.dialog.add.btn.add"));
        return okAction;
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        ConfigPropertiesPanel panel = new ConfigPropertiesPanel(module, newConfigMetadata, objectDescriptor, NEW_CONFIG);
        return ContainerFactory.makeItScrollable(panel);
    }
}

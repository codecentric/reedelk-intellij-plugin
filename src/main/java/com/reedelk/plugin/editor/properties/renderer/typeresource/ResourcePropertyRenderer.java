package com.reedelk.plugin.editor.properties.renderer.typeresource;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.module.descriptor.model.property.ResourceAwareDescriptor;
import com.reedelk.plugin.commons.Icons;
import com.reedelk.plugin.commons.PluginModuleUtils;
import com.reedelk.plugin.editor.properties.commons.ChooseFileInputField;
import com.reedelk.plugin.editor.properties.commons.ClickableLabel;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.commons.DisposablePanel;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.editor.properties.context.PropertyAccessor;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

import static com.intellij.util.ui.JBUI.Borders.emptyLeft;
import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.util.Optional.ofNullable;

public class ResourcePropertyRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        ResourceAwareDescriptor resourceAwareDescriptor = propertyDescriptor.getType();

        String chooseFileHint = ofNullable(resourceAwareDescriptor.getHintBrowseFile())
                .orElse(message("properties.type.resource.choose.file.hint"));

        String chooseFileDialogTitle = ofNullable(resourceAwareDescriptor.getHintBrowseFile())
                .orElse(message("properties.type.resource.choose.file.dialog"));

        // The root directory is {PROJECT_HOME}/src/main/resources because
        // we only want to allow users to select files from resources.
        String chooseFileRootDirectory = PluginModuleUtils.getResourcesDirectory(module)
                .orElseThrow(() -> new IllegalStateException(message("error.resource.dir.not.found")));

        ChooseFileInputField choseInput = new ChooseFileInputField(
                module.getProject(),
                chooseFileDialogTitle,
                chooseFileHint,
                chooseFileRootDirectory,
                propertyAccessor);

        // Erase Button clear the ChooseFileInputField value.
        ClickableLabel eraseButton = new ClickableLabel(Icons.Misc.Erase, () -> {
            propertyAccessor.set(null);
            choseInput.setText(null);
        });
        eraseButton.setBorder(emptyLeft(5));

        DisposablePanel wrapper = new DisposablePanel(new BorderLayout());
        wrapper.add(choseInput, BorderLayout.WEST);
        wrapper.add(eraseButton, BorderLayout.EAST);

        return Boolean.TRUE.equals(resourceAwareDescriptor.getWidthAuto()) ?
                ContainerFactory.pushCenter(wrapper) :
                ContainerFactory.pushLeft(wrapper);
    }
}

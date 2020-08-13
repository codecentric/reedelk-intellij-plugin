package com.reedelk.plugin.editor.properties.renderer.typeresource;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.module.descriptor.model.property.ResourceAwareDescriptor;
import com.reedelk.plugin.commons.PluginModuleUtils;
import com.reedelk.plugin.editor.properties.commons.ChooseFileInputFieldWithEraseBtn;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import com.reedelk.plugin.editor.properties.context.PropertyAccessor;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

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

        ChooseFileInputFieldWithEraseBtn chooseFileInputField =
                new ChooseFileInputFieldWithEraseBtn(
                        module.getProject(),
                        chooseFileDialogTitle,
                        chooseFileHint,
                        chooseFileRootDirectory,
                        propertyAccessor);

        return Boolean.TRUE.equals(resourceAwareDescriptor.getWidthAuto()) ?
                ContainerFactory.pushCenter(chooseFileInputField) :
                ContainerFactory.pushLeft(chooseFileInputField);
    }
}

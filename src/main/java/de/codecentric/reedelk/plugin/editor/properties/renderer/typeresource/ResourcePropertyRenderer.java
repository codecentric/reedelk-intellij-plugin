package de.codecentric.reedelk.plugin.editor.properties.renderer.typeresource;

import de.codecentric.reedelk.plugin.editor.properties.commons.ContainerFactory;
import de.codecentric.reedelk.plugin.editor.properties.commons.DisposablePanel;
import de.codecentric.reedelk.plugin.editor.properties.commons.FileChooseInputFieldWithEraseBtn;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.ResourceAwareDescriptor;
import de.codecentric.reedelk.plugin.commons.PluginModuleUtils;
import org.jetbrains.annotations.NotNull;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;
import static java.util.Optional.ofNullable;

public class ResourcePropertyRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public RenderedComponent render(@NotNull Module module,
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

        FileChooseInputFieldWithEraseBtn chooseFileInputField =
                new FileChooseInputFieldWithEraseBtn(
                        module.getProject(),
                        chooseFileDialogTitle,
                        chooseFileHint,
                        chooseFileRootDirectory,
                        propertyAccessor);

        DisposablePanel component = Boolean.TRUE.equals(resourceAwareDescriptor.getWidthAuto()) ?
                ContainerFactory.pushCenter(chooseFileInputField) :
                ContainerFactory.pushLeft(chooseFileInputField);

        return RenderedComponent.create(component);
    }
}

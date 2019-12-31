package com.reedelk.plugin.editor.properties.renderer.typeresource;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBTextField;
import com.reedelk.module.descriptor.model.ComponentPropertyDescriptor;
import com.reedelk.plugin.commons.PluginModuleUtils;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.ContainerFactory;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class ResourcePropertyRenderer extends AbstractPropertyTypeRenderer {

    private final int inputFileFieldColumns = 40;

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull ComponentPropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {


        String resourcesFolder = PluginModuleUtils.getResourcesFolder(module)
                .orElseThrow(() -> new IllegalStateException(message("error.resource.dir.not.found")));

        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, true, true, true, false, false) {
            @Override
            public void validateSelectedFiles(VirtualFile[] files) {
                if (!files[0].getPresentableUrl().startsWith(resourcesFolder)) {
                    // IMPORTANT: The file needs to belong to the current module
                    throw new IllegalArgumentException(message("properties.type.resource.choose.file.dialog.error.not.resource"));
                }
            }
        };

        descriptor.setTitle(message("properties.type.resource.choose.file.dialog"));

        JBTextField textField =
                new ChooseFileInputField(message("properties.type.resource.choose.file.hint"), inputFileFieldColumns);

        textField.setEditable(false);

        TextFieldWithBrowse choseFile = new TextFieldWithBrowse(textField);
        choseFile.setText(propertyAccessor.get());
        choseFile.setEditable(false);

        TextBrowseFolderListener actionListener =
                new TextBrowseFolderListener(descriptor, module.getProject(), choseFile, resourcesFolder, propertyAccessor);
        choseFile.addCustomBrowseFolderListener(actionListener);

        return ContainerFactory.pushLeft(choseFile);
    }


    static class TextBrowseFolderListener extends ComponentWithBrowseButton.BrowseFolderActionListener<JTextField> {
        TextBrowseFolderListener(@NotNull FileChooserDescriptor fileChooserDescriptor,
                                 @Nullable Project project,
                                 @NotNull ComponentWithBrowseButton textField,
                                 String resourcesFolder,
                                 PropertyAccessor propertyAccessor) {
            super(null, null, textField, project, fileChooserDescriptor, new FileComponentPropertyAccessor(resourcesFolder, propertyAccessor));
        }

        FileChooserDescriptor getFileChooserDescriptor() {
            return myFileChooserDescriptor;
        }
    }

    static class TextFieldWithBrowse extends TextFieldWithBrowseButton {

        TextFieldWithBrowse(JBTextField textField) {
            // to prevent field to be infinitely re-sized in grid-box layouts
            super(textField, null);
        }

        void addCustomBrowseFolderListener(@NotNull TextBrowseFolderListener listener) {
            addActionListener(listener);
            installPathCompletion(listener.getFileChooserDescriptor());
        }
    }

    static class FileComponentPropertyAccessor implements TextComponentAccessor<JTextField> {

        private final String root;
        private final PropertyAccessor propertyAccessor;

        FileComponentPropertyAccessor(String root, PropertyAccessor propertyAccessor) {
            this.root = root;
            this.propertyAccessor = propertyAccessor;
        }

        @Override
        public String getText(JTextField component) {
            String filePath = propertyAccessor.get();
            return filePath == null ?
                    root + "/" :
                    root + "/" + filePath;
        }

        @Override
        public void setText(JTextField component, @NotNull String text) {
            String fileRelativePath = text.substring(root.length() + 1);
            component.setText(fileRelativePath);
            propertyAccessor.set(fileRelativePath);
        }
    }
}

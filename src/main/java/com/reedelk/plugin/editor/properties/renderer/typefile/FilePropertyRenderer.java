package com.reedelk.plugin.editor.properties.renderer.typefile;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.commons.ModuleUtils;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionListener;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class FilePropertyRenderer extends AbstractPropertyTypeRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull ComponentPropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {


        String resourcesFolder = ModuleUtils.getResourcesFolder(module)
                .orElseThrow(() -> new IllegalStateException("The project must have a resource folder defined in the project."));

        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, true, true, true, false, false) {
            @Override
            public void validateSelectedFiles(VirtualFile[] files) {
                // The file needs to belong to the current module!!!
                if (!files[0].getPresentableUrl().startsWith(resourcesFolder)) {
                    throw new IllegalArgumentException("A file must be selected from your project/resources folder");
                }
            }
        };

        descriptor.setTitle(message("properties.type.file.choose.file"));

        TextFieldWithBrowse choseFile = new TextFieldWithBrowse();

        choseFile.setText(propertyAccessor.get());

        choseFile.addCustomBrowseFolderListener(
                new TextBrowseFolderListener(descriptor, module.getProject(), choseFile, resourcesFolder, propertyAccessor));

        return choseFile;
    }


    class TextBrowseFolderListener extends ComponentWithBrowseButton.BrowseFolderActionListener<JTextField> {
        TextBrowseFolderListener(@NotNull FileChooserDescriptor fileChooserDescriptor,
                                 @Nullable Project project,
                                 @NotNull ComponentWithBrowseButton textField,
                                 String boh,
                                 PropertyAccessor propertyAccessor) {
            super(null, null, textField, project, fileChooserDescriptor, new FileComponentPropertyAccessor(boh, propertyAccessor));
        }

        FileChooserDescriptor getFileChooserDescriptor() {
            return myFileChooserDescriptor;
        }
    }

    class TextFieldWithBrowse extends TextFieldWithBrowseButton {
        TextFieldWithBrowse() {
            super((ActionListener) null);
        }

        void addCustomBrowseFolderListener(@NotNull TextBrowseFolderListener listener) {
            addActionListener(listener);
            installPathCompletion(listener.getFileChooserDescriptor());
        }
    }

    class FileComponentPropertyAccessor implements TextComponentAccessor<JTextField> {

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

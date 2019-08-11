package com.reedelk.plugin.editor.properties.renderer;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.commons.Labels;
import com.reedelk.plugin.commons.ModuleUtils;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.widget.input.script.PropertyPanelContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionListener;

public class TypeFilePropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(Module module, ComponentPropertyDescriptor propertyDescriptor, PropertyAccessor propertyAccessor, PropertyPanelContext propertyPanelContext) {

        String resourcesFolder = ModuleUtils.getResourcesFolder(module).get();

        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, true, true, true, false, false) {
            @Override
            public void validateSelectedFiles(VirtualFile[] files) throws Exception {
                // The file needs to belong to the current module!!!
                if (!files[0].getPresentableUrl().startsWith(resourcesFolder)) {
                    throw new Exception("A file must be selected from your project/resources folder");
                }
            }
        };

        descriptor.setTitle(Labels.FILE_CHOOSER_TITLE);

        TextFieldWithBrowse choseFile = new TextFieldWithBrowse();

        ModuleUtils.getResourcesFolder(module)
                .ifPresent(resourcesFolderPath ->
                        choseFile.setText(propertyAccessor.get()));


        choseFile.addBrowseFolderListener(
                new TextBrowseFolderListener(descriptor, module.getProject(), resourcesFolder, propertyAccessor));

        return choseFile;
    }


    class TextBrowseFolderListener extends ComponentWithBrowseButton.BrowseFolderActionListener<JTextField> {
        TextBrowseFolderListener(@NotNull FileChooserDescriptor fileChooserDescriptor, @Nullable Project project, String boh, PropertyAccessor propertyAccessor) {
            super(null, null, null, project, fileChooserDescriptor, new FileComponentPropertyAccessor(boh, propertyAccessor));
        }

        void setOwnerComponent(@NotNull TextFieldWithBrowseButton component) {
            myTextComponent = component;
        }

        FileChooserDescriptor getFileChooserDescriptor() {
            return myFileChooserDescriptor;
        }
    }

    class TextFieldWithBrowse extends TextFieldWithBrowseButton {
        TextFieldWithBrowse() {
            super((ActionListener) null);
        }

        void addBrowseFolderListener(@NotNull TextBrowseFolderListener listener) {
            listener.setOwnerComponent(this);
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

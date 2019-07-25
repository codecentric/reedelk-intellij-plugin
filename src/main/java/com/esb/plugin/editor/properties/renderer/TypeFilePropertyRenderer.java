package com.esb.plugin.editor.properties.renderer;

import com.esb.plugin.commons.Colors;
import com.esb.plugin.commons.Labels;
import com.esb.plugin.commons.ModuleUtils;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.esb.plugin.editor.properties.widget.PropertyPanelContext;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComponentWithBrowseButton;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
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

        MyTextFieldWithBrowse choseFile = new MyTextFieldWithBrowse();


        ModuleUtils.getResourcesFolder(module)
                .ifPresent(resourcesFolderPath ->
                        choseFile.setText(propertyAccessor.get()));


        choseFile.addBrowseFolderListener(
                new MyTextBrowseFolderListener(descriptor, module.getProject(), resourcesFolder, propertyAccessor));

        return choseFile;
    }


    class MyTextBrowseFolderListener extends ComponentWithBrowseButton.BrowseFolderActionListener<JTextField> {

        MyTextBrowseFolderListener(@NotNull FileChooserDescriptor fileChooserDescriptor, @Nullable Project project, String boh, PropertyAccessor propertyAccessor) {
            super(null, null, null, project, fileChooserDescriptor, new MyTextComponentAccessor(boh, propertyAccessor));
        }

        void setOwnerComponent(@NotNull TextFieldWithBrowseButton component) {
            myTextComponent = component;
        }

        FileChooserDescriptor getFileChooserDescriptor() {
            return myFileChooserDescriptor;
        }
    }

    class MyTextComponentAccessor implements TextComponentAccessor<JTextField> {

        private final String root;
        private final PropertyAccessor propertyAccessor;

        MyTextComponentAccessor(String root, PropertyAccessor propertyAccessor) {
            this.root = root;
            this.propertyAccessor = propertyAccessor;
        }

        @Override
        public String getText(JTextField component) {
            String filePath = propertyAccessor.get();
            if (filePath == null) {
                return root + "/";
            } else {
                return root + "/" + filePath;
            }
        }

        @Override
        public void setText(JTextField component, @NotNull String text) {
            String fileRelativePath = text.substring(root.length() + 1);
            component.setText(fileRelativePath);
            propertyAccessor.set(fileRelativePath);
        }
    }

    class MyTextFieldWithBrowse extends TextFieldWithBrowseButton {

        public MyTextFieldWithBrowse() {
            super((ActionListener) null);
            setBackground(Colors.PROPERTIES_BACKGROUND);
        }

        void addBrowseFolderListener(@NotNull MyTextBrowseFolderListener listener) {
            listener.setOwnerComponent(this);
            addActionListener(listener);
            installPathCompletion(listener.getFileChooserDescriptor());
        }
    }
}

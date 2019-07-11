package com.esb.plugin.editor.properties.renderer;

import com.esb.plugin.commons.Labels;
import com.esb.plugin.commons.MavenUtils;
import com.esb.plugin.component.domain.ComponentPropertyDescriptor;
import com.esb.plugin.component.domain.TypeFileDescriptor;
import com.esb.plugin.editor.properties.accessor.PropertyAccessor;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.project.MavenProject;

import javax.swing.*;
import java.util.Optional;

public class TypeFilePropertyRenderer implements TypePropertyRenderer {

    @Override
    public JComponent render(Module module, ComponentPropertyDescriptor propertyDescriptor, PropertyAccessor propertyAccessor) {
        TypeFileDescriptor typeFileDescriptor = (TypeFileDescriptor) propertyDescriptor.getPropertyType();

        FileChooserDescriptor descriptor = FileChooserDescriptorFactory.createSingleFileDescriptor();

        descriptor.setTitle(Labels.FILE_CHOOSER_TITLE);

        Optional<MavenProject> mavenProject = MavenUtils.getMavenProject(module);

        VirtualFile moduleRootFolder = mavenProject.get().getFile().getParent();

        TextFieldWithBrowseButton choseFile = new TextFieldWithBrowseButton();
        choseFile.setText(moduleRootFolder.getPath() + "/src/main/resources/metadata");

        choseFile.addBrowseFolderListener(new TextBrowseFolderListener(descriptor, module.getProject()) {
            @NotNull
            @Override
            protected String chosenFileToResultingText(@NotNull VirtualFile chosenFile) {
                return chosenFile.getPath();
            }
        });
        return choseFile;
    }
}

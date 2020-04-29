package com.reedelk.plugin.builder;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;

import static com.reedelk.plugin.message.ReedelkBundle.message;

abstract class AbstractProjectBuilderHelper {

    private static final Logger LOG = Logger.getInstance(MavenProjectBuilderHelper.class);

    Optional<VirtualFile> createFromTemplate(Project project, String templateName, Properties templateProperties, VirtualFile destinationDir, String fileName) {
        FileTemplateManager manager = FileTemplateManager.getInstance(project);
        FileTemplate fileTemplate = manager.getInternalTemplate(templateName);
        try {
            String fileText = fileTemplate.getText(templateProperties);
            VirtualFile file = destinationDir.findOrCreateChildData(this, fileName);
            VfsUtil.saveText(file, fileText);
            return Optional.of(file);
        } catch (IOException exception) {
            String message = message("moduleBuilder.hello.world.template.error", templateName, exception.getMessage());
            LOG.warn(message, exception);
            return Optional.empty();
        }
    }

    Optional<VirtualFile> createFromTemplate(Project project, String templateName, Properties templateProperties, VirtualFile destinationDir) {
        return createFromTemplate(project, templateName, templateProperties, destinationDir, templateName);
    }

    Optional<VirtualFile> createDirectory(VirtualFile root, String suffix) {
        try {
            String finalDirectoryPath = Paths.get(root.getPath(), suffix).toString();
            return Optional.ofNullable(VfsUtil.createDirectories(finalDirectoryPath));
        } catch (IOException exception) {
            String message = message("moduleBuilder.hello.world.create.dir.error", suffix, exception.getMessage());
            LOG.warn(message, exception);
            return Optional.empty();
        }
    }

    Optional<VirtualFile> createFromTemplate(Project project, String templateName, VirtualFile destinationDir) {
        Properties emptyProperties = new Properties();
        return createFromTemplate(project, templateName, emptyProperties, destinationDir);
    }
}

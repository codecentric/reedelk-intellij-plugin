package com.reedelk.plugin.builder;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import static com.reedelk.plugin.message.ReedelkBundle.message;

abstract class AbstractProjectBuilderHelper {

    private static final Logger LOG = Logger.getInstance(MavenProjectBuilderHelper.class);

    void createFromTemplate(Project project, String templateName, Properties templateProperties, VirtualFile destinationDir, String fileName) {
        FileTemplateManager manager = FileTemplateManager.getInstance(project);
        FileTemplate fileTemplate = manager.getInternalTemplate(templateName);
        try {
            String scriptText = fileTemplate.getText(templateProperties);
            VirtualFile scriptFile = destinationDir.findOrCreateChildData(this, fileName);
            VfsUtil.saveText(scriptFile, scriptText);
        } catch (IOException exception) {
            String message = message("moduleBuilder.hello.world.template.error", templateName, exception.getMessage());
            LOG.warn(message, exception);
        }
    }

    void createFromTemplate(Project project, String templateName, Properties templateProperties, VirtualFile destinationDir) {
        createFromTemplate(project, templateName, templateProperties, destinationDir, templateName);
    }

    void createFromTemplate(Project project, String templateName, VirtualFile destinationDir) {
        Properties emptyProperties = new Properties();
        createFromTemplate(project, templateName, emptyProperties, destinationDir);
    }

    Optional<VirtualFile> createDirectory(VirtualFile root, String suffix) {
        try {
            return Optional.ofNullable(VfsUtil.createDirectories(root.getPath() + suffix));
        } catch (IOException exception) {
            String message = message("moduleBuilder.hello.world.create.dir.error", suffix, exception.getMessage());
            LOG.warn(message, exception);
            return Optional.empty();
        }
    }
}

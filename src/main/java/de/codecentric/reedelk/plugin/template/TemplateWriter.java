package de.codecentric.reedelk.plugin.template;

import de.codecentric.reedelk.plugin.message.ReedelkBundle;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import static de.codecentric.reedelk.plugin.message.ReedelkBundle.message;

public interface TemplateWriter {

    Logger LOG = Logger.getInstance(TemplateWriter.class);

    String templateName();

    default WritingStrategy writingStrategy() {
        return new WritingStrategyOverride();
    }

    default Optional<VirtualFile> create(Project project, Properties templateProperties, VirtualFile destinationDir, String fileName) {
        FileTemplateManager manager = FileTemplateManager.getInstance(project);
        FileTemplate fileTemplate = manager.getInternalTemplate(templateName());
        try {
            String fileText = fileTemplate.getText(templateProperties);
            return writingStrategy().write(fileText, destinationDir, fileName);
        } catch (IOException exception) {
            String message = ReedelkBundle.message("template.error", templateName(), exception.getMessage());
            LOG.warn(message, exception);
            return Optional.empty();
        }
    }

    default Optional<VirtualFile> create(Project project, Properties templateProperties, VirtualFile destinationDir) {
        return create(project, templateProperties, destinationDir, templateName());
    }

    default Optional<VirtualFile> create(Project project, VirtualFile destinationDir, String fileName) {
        Properties emptyProperties = new Properties();
        return create(project, emptyProperties, destinationDir, fileName);
    }

    default Optional<VirtualFile> create(Project project, VirtualFile destinationDir) {
        Properties emptyProperties = new Properties();
        return create(project, emptyProperties, destinationDir);
    }
}

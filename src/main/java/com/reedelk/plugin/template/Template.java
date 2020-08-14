package com.reedelk.plugin.template;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

import static com.reedelk.plugin.message.ReedelkBundle.message;

public class Template {

    private static final Logger LOG = Logger.getInstance(Template.class);

    private Template() {
    }

    public enum Maven implements Buildable {

        PROJECT("MavenProject"),
        MODULE("MavenModule");

        private final String templateName;

        Maven(String templateName) {
            this.templateName = templateName;
        }

        @Override
        public String templateName() {
            return templateName;
        }
    }

    public enum ProjectFile implements Buildable {

        FLOW("FlowFile"),
        SUBFLOW("SubflowFile");

        private final String templateName;

        ProjectFile(String templateName) {
            this.templateName = templateName;
        }

        @Override
        public String templateName() {
            return templateName;
        }
    }

    public enum HelloWorld implements Buildable {

        GET_FLOW("GETHelloWorld.flow"),
        POST_FLOW("POSTHelloWorld.flow"),
        SCRIPT("helloWorld.groovy"),
        CONFIG("DefaultListener.fconfig"),
        GIT_IGNORE(".gitignore"),
        DOCKERFILE("Dockerfile.txt"),
        HEROKU_PROCFILE("Procfile.txt");

        private final String templateName;

        HelloWorld(String templateName) {
            this.templateName = templateName;
        }

        @Override
        public String templateName() {
            return templateName;
        }
    }

    public enum OpenAPI implements Buildable {

        REST_LISTENER_CONFIG("OpenAPIConfig.fconfig"),
        FLOW_WITH_REST_LISTENER("OpenAPI.flow"),
        FLOW_WITH_REST_LISTENER_AND_RESOURCE("OpenAPIRESTListenerWithResource.flow"),
        EXAMPLE("OpenAPIExample.txt"),
        ASSET("OpenAPIAsset.txt");

        private final String templateName;

        OpenAPI(String templateName) {
            this.templateName = templateName;
        }

        @Override
        public String templateName() {
            return templateName;
        }
    }

    public interface Buildable {

        String templateName();

        default Optional<VirtualFile> create(Project project, Properties templateProperties, VirtualFile destinationDir, String fileName) {
            FileTemplateManager manager = FileTemplateManager.getInstance(project);
            FileTemplate fileTemplate = manager.getInternalTemplate(templateName());
            try {
                String fileText = fileTemplate.getText(templateProperties);

                VirtualFile file = destinationDir.findOrCreateChildData(this, fileName);
                Document document = FileDocumentManager.getInstance().getDocument(file);

                if (document != null) {
                    // File already cached, therefore we must update the cached file and *not*
                    // the one in the file system.
                    document.setText(fileText);
                } else {
                    // File not cached, we can save the text on the file system.
                    VfsUtil.saveText(file, fileText);
                }

                return Optional.of(file);
            } catch (IOException exception) {
                String message = message("template.error", templateName(), exception.getMessage());
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
}

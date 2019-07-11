package com.esb.plugin.module.builder;

import com.esb.plugin.commons.Template;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ThrowableRunnable;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.model.MavenId;

import java.io.IOException;
import java.util.Properties;

import static com.esb.internal.commons.ModuleProperties.*;

class ESBMavenProjectBuilderHelper {

    private static final Logger LOG = Logger.getInstance(ESBMavenProjectBuilderHelper.class);

    void configure(Project project, MavenId projectId, @Nullable MavenId parentId, VirtualFile root, String sdkVersion) throws Throwable {

        if (parentId == null) {
            // Parent ID IS NULL
            MavenProjectProperties props = new MavenProjectProperties(projectId, sdkVersion);
            createFromTemplate(project, props, Template.Internal.MAVEN_PROJECT, root);
        } else {
            // Parent ID is NOT NULL
            MavenProjectProperties props = new MavenProjectProperties(projectId, sdkVersion, parentId);
            createFromTemplate(project, props, Template.Internal.MAVEN_MODULE, root);
        }

    }

    private void createFromTemplate(Project project, Properties properties, String templateName, VirtualFile root) throws Throwable {
        WriteCommandAction.writeCommandAction(project).run(new ThrowableRunnable<Throwable>() {
                    @Override
                    public void run() throws Throwable {
                        final FileTemplateManager manager = FileTemplateManager.getInstance(project);

                        final Properties defaultProperties = manager.getDefaultProperties();
                        defaultProperties.putAll(properties);

                        final FileTemplate template = manager.getInternalTemplate(templateName);
                        final String text = template.getText(defaultProperties);

                        VirtualFile pomFile = root.findOrCreateChildData(this, MavenConstants.POM_XML);
                        VfsUtil.saveText(pomFile, text);
                    }
                });

        ProjectDirectoryGenerator.createDirectories(root);
    }

    class MavenProjectProperties extends Properties {
        MavenProjectProperties(MavenId projectId, String sdkVersion) {
            setProperty("groupId", projectId.getGroupId());
            setProperty("artifactId", projectId.getArtifactId());
            setProperty("version", projectId.getVersion());
            setProperty("javaVersion", sdkVersion);
        }

        MavenProjectProperties(MavenId projectId, String sdkVersion, MavenId parentId) {
            this(projectId, sdkVersion);
            setProperty("parentId", parentId.getArtifactId());
            setProperty("parentVersion", parentId.getVersion());
        }
    }

    private static class ProjectDirectoryGenerator {

        private static final String BASE_RESOURCE_FOLDER = "/src/main/resources";

        private static void createDirectories(VirtualFile root) {
            createDirectory(root, BASE_RESOURCE_FOLDER + Flow.RESOURCE_DIRECTORY);
            createDirectory(root, BASE_RESOURCE_FOLDER + Config.RESOURCE_DIRECTORY);
            createDirectory(root, BASE_RESOURCE_FOLDER + Metadata.RESOURCE_DIRECTORY);
        }

        private static void createDirectory(VirtualFile root, String suffix) {
            try {
                VfsUtil.createDirectories(root.getPath() + suffix);
            } catch (IOException e) {
                LOG.info(String.format("Could not create project directory for root=%s and suffix=%s", root.getPath(), suffix), e);
            }
        }
    }
}

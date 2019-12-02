package com.reedelk.plugin.builder;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ThrowableRunnable;
import com.reedelk.plugin.commons.Template;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.model.MavenId;

import java.util.Properties;

class MavenProjectBuilderHelper {

    void configure(Project project, MavenId projectId, @Nullable MavenId parentId, VirtualFile root, String sdkVersion) throws Throwable {
        if (parentId == null) {
            // Parent ID IS NULL (configuring a new project)
            MavenProjectProperties props = new MavenProjectProperties(projectId, sdkVersion);
            createFromTemplate(project, props, Template.Maven.PROJECT, root);
        } else {
            // Parent ID is NOT NULL (configuring a new module)
            MavenProjectProperties props = new MavenProjectProperties(projectId, sdkVersion, parentId);
            createFromTemplate(project, props, Template.Maven.MODULE, root);
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
}

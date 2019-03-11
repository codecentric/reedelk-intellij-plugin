package com.esb.plugin.module;

import com.esb.plugin.Template;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.application.Result;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.model.MavenId;

import java.io.IOException;
import java.util.Properties;

public class ESBMavenProjectBuilderHelper {

    public void configure(Project project, MavenId projectId, @Nullable MavenId parentId, VirtualFile root) {

        String groupId = projectId.getGroupId();
        String artifactId = projectId.getArtifactId();
        String version = projectId.getVersion();

        if (parentId == null) {
            createPomFile(project, projectId, root);
        } else {
            //createModulePomFile(project, projectId, root, parentId);
        }



        System.out.println(groupId + " " + artifactId + " " + version);

    }

    private VirtualFile createPomFile(final Project project, final MavenId projectId, final VirtualFile root) {
        return new WriteCommandAction<VirtualFile>(project, "Create ESB Project", PsiFile.EMPTY_ARRAY) {
            @Override
            protected void run(@NotNull Result<VirtualFile> result) throws Throwable {

                try {
                    VirtualFile pomFile = root.findOrCreateChildData(this, MavenConstants.POM_XML);
                    final Properties templateProps = new Properties();
                    templateProps.setProperty("groupId", projectId.getGroupId());
                    templateProps.setProperty("artifactId", projectId.getArtifactId());
                    templateProps.setProperty("version", projectId.getVersion());
                    templateProps.setProperty("javaVersion", "1.8");
                    final FileTemplateManager manager = FileTemplateManager.getInstance(project);
                    final FileTemplate template = manager.getInternalTemplate(Template.Internal.MAVEN_PROJECT);
                    final Properties defaultProperties = manager.getDefaultProperties();
                    defaultProperties.putAll(templateProps);
                    final String text = template.getText(defaultProperties);
                    VfsUtil.saveText(pomFile, text);
                    result.setResult(pomFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    //showError(project, e);
                }
            }
        }.execute().getResultObject();
    }
}

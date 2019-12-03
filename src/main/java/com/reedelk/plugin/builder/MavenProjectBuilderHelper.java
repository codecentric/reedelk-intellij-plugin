package com.reedelk.plugin.builder;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.template.MavenProjectProperties;
import com.reedelk.plugin.template.Template.Maven;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.model.MavenId;

class MavenProjectBuilderHelper extends AbstractProjectBuilderHelper {

    void configure(Project project, MavenId projectId, @Nullable MavenId parentId, VirtualFile root, String sdkVersion) {

        if (parentId == null) {
            // Parent ID IS NULL (configuring a new project)
            MavenProjectProperties templateProperties = new MavenProjectProperties(projectId, sdkVersion);
            WriteCommandAction.runWriteCommandAction(project, () ->
                    createFromTemplate(project, Maven.PROJECT, templateProperties, root, MavenConstants.POM_XML)
                            .ifPresent(virtualFile -> {
                                // nothing to do
                            }));

        } else {
            // Parent ID is NOT NULL (configuring a new module)
            MavenProjectProperties templateProperties = new MavenProjectProperties(projectId, sdkVersion, parentId);
            WriteCommandAction.runWriteCommandAction(project, () ->
                    createFromTemplate(project, Maven.MODULE, templateProperties, root, MavenConstants.POM_XML)
                            .ifPresent(virtualFile -> {
                                // nothing to do
                            }));
        }
    }
}

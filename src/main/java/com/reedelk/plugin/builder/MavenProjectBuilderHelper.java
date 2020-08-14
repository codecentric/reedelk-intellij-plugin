package com.reedelk.plugin.builder;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.template.MavenProjectProperties;
import com.reedelk.plugin.template.Template;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.model.MavenId;

class MavenProjectBuilderHelper {

    private final String reedelkRuntimeVersion;

    public MavenProjectBuilderHelper(String reedelkRuntimeVersion) {
        super();
        this.reedelkRuntimeVersion = reedelkRuntimeVersion;
    }

    void configure(Project project, MavenId projectId, @Nullable MavenId parentId, VirtualFile root, String sdkVersion) {

        if (parentId == null) {
            // Parent ID IS NULL (configuring a new project)
            MavenProjectProperties templateProperties = new MavenProjectProperties(projectId, sdkVersion, reedelkRuntimeVersion);
            WriteCommandAction.runWriteCommandAction(project, () -> {
                Template.Maven.PROJECT.create(project, templateProperties, root, MavenConstants.POM_XML);
            });

        } else {
            // Parent ID is NOT NULL (configuring a new module)
            MavenProjectProperties templateProperties = new MavenProjectProperties(projectId, sdkVersion, reedelkRuntimeVersion, parentId);
            WriteCommandAction.runWriteCommandAction(project, () -> {
                Template.Maven.MODULE.create(project, templateProperties, root, MavenConstants.POM_XML);
            });
        }
    }
}

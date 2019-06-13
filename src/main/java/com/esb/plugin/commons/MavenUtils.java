package com.esb.plugin.commons;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.io.File;
import java.util.Optional;

public class MavenUtils {

    public static Optional<MavenProject> getMavenProject(Project project, String moduleName) {
       Optional<String> optionalPomXml = getModulePomXml(moduleName, project);
       if (optionalPomXml.isPresent()) {
           VirtualFile file = LocalFileSystem.getInstance().findFileByPath(optionalPomXml.get());
           if (file != null) {
               MavenProject mavenProject = MavenProjectsManager.getInstance(project).findProject(file);
               return Optional.ofNullable(mavenProject);
           }
       }
       return Optional.empty();
    }

    private static Optional<String> getModulePomXml(String moduleName, Project project) {
        Module moduleByName = findModuleByName(moduleName, project);
        if (moduleByName != null) {
            String moduleFilePath = moduleByName.getModuleFilePath();
            String modulePomXml = new File(moduleFilePath).getParent() + "/" + MavenConstants.POM_XML;
            return Optional.of(modulePomXml);
        }
        return Optional.empty();
    }

    private static Module findModuleByName(String name, Project project) {
        return ModuleManager.getInstance(project).findModuleByName(name);
    }
}

package com.esb.plugin.utils;

import com.esb.plugin.service.project.filechange.ESBFileChangeService;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.util.Optional;

public class ESBModuleUtils {


    public static boolean isHotSwap(Project project, String moduleName) {
        ESBFileChangeService fileChangeService = ServiceManager.getService(project, ESBFileChangeService.class);
        return !fileChangeService.isCompileRequired(moduleName);
    }

    public static void unchanged(Project project, String moduleName) {
        ESBFileChangeService fileChangeService = ServiceManager.getService(project, ESBFileChangeService.class);
        fileChangeService.unchanged(moduleName);
    }

    public static void changed(Project project, String moduleName) {
        ESBFileChangeService fileChangeService = ServiceManager.getService(project, ESBFileChangeService.class);
        fileChangeService.changed(moduleName);
    }

    public static Optional<MavenProject> getMavenProject(String moduleName, Project project) {
       Optional<String> optionalPomXml = getModulePomXml(moduleName, project);
       if (optionalPomXml.isPresent()) {
           VirtualFile file = LocalFileSystem.getInstance().findFileByPath(optionalPomXml.get());
           if (file != null) {
               return Optional.ofNullable(MavenProjectsManager.getInstance(project).findProject(file));
           }
       }
       return Optional.empty();
    }

    public static Module findModuleByName(String name, Project project) {
        return ModuleManager.getInstance(project).findModuleByName(name);
    }

    public static Optional<String> getModulePomXml(String moduleName, Project project) {
        Module moduleByName = findModuleByName(moduleName, project);
        if (moduleByName != null) {
            VirtualFile moduleFile = moduleByName.getModuleFile();
            if (moduleFile != null) {
                String modulePomXml = moduleFile.getParent().getPath() + "/" + MavenConstants.POM_XML;
                return Optional.of(modulePomXml);
            }
        }
        return Optional.empty();
    }

}

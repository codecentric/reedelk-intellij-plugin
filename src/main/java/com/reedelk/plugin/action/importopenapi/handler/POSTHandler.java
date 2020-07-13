package com.reedelk.plugin.action.importopenapi.handler;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.plugin.commons.PluginModuleUtils;
import com.reedelk.plugin.template.Template;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import org.jetbrains.annotations.SystemIndependent;

import java.nio.file.Paths;
import java.util.Optional;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;

public class POSTHandler implements Handler {

    @Override
    public boolean isApplicable(PathItem pathItem) {
        return pathItem.getPost() != null;
    }

    @Override
    public void accept(Project project, String pathEntry, PathItem pathItem) {
        String description = pathItem.getDescription();
        Operation postOperation = pathItem.getPost();
        String operationId = postOperation.getOperationId();
        String operationDescription = postOperation.getDescription();

        // Flow ID
        // Flow Title
        // Flow Description
        // Operation Description
        // Config ID
        // Operation Path
        // Operation Method
        Properties properties = new Properties();
        properties.put("id", UUID.randomUUID().toString());
        properties.put("title", "Flow title");
        if (operationDescription != null) {
            properties.put("description", operationDescription);
            properties.put("operationDescription", operationDescription);
        }
        properties.put("configId", "");
        properties.put("operationPath", pathEntry);
        properties.put("operationMethod", "POST");

        @SystemIndependent String basePath = project.getBasePath();
        VirtualFile parent = project.getProjectFile().getParent().getParent();

        Module[] modules = ModuleManager.getInstance(project).getModules();
        Module module = modules[0];

        Optional<String> flowsFolder = PluginModuleUtils.getFlowsFolder(module);
        flowsFolder.ifPresent(new Consumer<String>() {
            @Override
            public void accept(String flowsFolder) {
                VirtualFile flowsFolderVf = VfsUtil.findFile(Paths.get(flowsFolder), true);
                Template.OpenAPI.FLOW_WITH_REST_LISTENER.create(project, properties, flowsFolderVf, operationId + ".flow");
            }
        });
    }
}

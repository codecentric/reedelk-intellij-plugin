package com.reedelk.plugin.action.openapi;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.openapi.v3.model.RequestBodyObject;
import com.reedelk.openapi.v3.model.SchemaObject;
import com.reedelk.plugin.commons.PluginModuleUtils;
import com.reedelk.plugin.template.ConfigProperties;
import com.reedelk.plugin.template.Template;
import com.reedelk.runtime.api.commons.StringUtils;
import com.reedelk.runtime.commons.ModuleProperties;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class OpenApiImporterContext {

    private final String openApiFilePath;
    private final String importModuleName;
    private final String targetDirectory;
    private final Project project;
    private final String configId = UUID.randomUUID().toString();

    private Map<String,String> schemaIdAndPath = new HashMap<>();
    private Map<String, RequestBodyObject> requestBodyIdAndData = new HashMap<>();

    public OpenApiImporterContext(@NotNull Project project, String openAPIFilePath, String importModuleName, String targetDirectory) {
        this.project = project;
        this.targetDirectory = targetDirectory;
        this.openApiFilePath = openAPIFilePath;
        this.importModuleName = importModuleName;
    }

    public String getOpenApiFilePath() {
        return openApiFilePath;
    }

    public OpenApiSchemaFormat getSchemaFormat() {
        return OpenApiSchemaFormat.formatOf(openApiFilePath);
    }

    public String getConfigId() {
        return configId;
    }

    public Optional<String> createAsset(String schemaId, SchemaObject schemaObject, OpenApiSchemaFormat schemaFormat) {
        if (schemaObject.getSchema() != null) {
            Map<String, Object> schemaData = schemaObject.getSchema().getSchemaData();

            Properties properties = new Properties();
            if (OpenApiSchemaFormat.JSON.equals(schemaFormat)) {
                properties.put("data", new JSONObject(schemaData).toString(2));
            }

            if (OpenApiSchemaFormat.YAML.equals(schemaFormat)) {
                properties.put("data", new Yaml().dump(schemaData));
            }


            Module module = getImportModule();

            String finalFileName = schemaId + "." + schemaFormat.getExtension();
            Optional<String> assetsDirectory = PluginModuleUtils.getAssetsDirectory(module);
            assetsDirectory.ifPresent(directory -> WriteCommandAction.runWriteCommandAction(project, () -> {

                Path targetDirectory = Paths.get(directory, this.targetDirectory);
                try {
                    VfsUtil.createDirectoryIfMissing(targetDirectory.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                VirtualFile flowsFolderVf = VfsUtil.findFile(targetDirectory, true);
                Template.OpenAPI.ASSET.create(project, properties, flowsFolderVf, finalFileName)
                        .ifPresent(virtualFile -> FileEditorManager.getInstance(project)
                                .openFile(virtualFile, false));
            }));

            return  Optional.of(assetResource(finalFileName));
        }

        return Optional.empty();
    }

    public String createAsset(String fileName, String data) {
        Module module = getImportModule();

        Properties properties = new Properties();
        properties.put("data", data);

        Optional<String> assetsDirectory = PluginModuleUtils.getAssetsDirectory(module);
        assetsDirectory.ifPresent(directory -> WriteCommandAction.runWriteCommandAction(project, () -> {

            Path targetDirectory = Paths.get(directory, this.targetDirectory);
            try {
                VfsUtil.createDirectoryIfMissing(targetDirectory.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            VirtualFile assetsDirectoryVf = VfsUtil.findFile(targetDirectory, true);
            Template.OpenAPI.ASSET.create(project, properties, assetsDirectoryVf, fileName);
        }));

        return assetResource(fileName);
    }

    public void createFlow(String fileName, Properties properties) {
        Module module = getImportModule();

        Optional<String> flowsDirectory = PluginModuleUtils.getFlowsDirectory(module);
        flowsDirectory.ifPresent(directory -> WriteCommandAction.runWriteCommandAction(project, () -> {

            Path targetDirectory = Paths.get(directory, this.targetDirectory);
            try {
                VfsUtil.createDirectoryIfMissing(targetDirectory.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            VirtualFile flowsFolderVf = VfsUtil.findFile(targetDirectory, true);
            Template.OpenAPI.FLOW_WITH_REST_LISTENER.create(project, properties, flowsFolderVf, fileName)
                    .ifPresent(virtualFile -> FileEditorManager.getInstance(project)
                            .openFile(virtualFile, true));
        }));
    }

    public void createConfig(String configFileName, String configOpenApi) {
        String title = "Open API Config";

        Module module = getImportModule();

        Optional<String> configsFolder = PluginModuleUtils.getConfigsDirectory(module);
        configsFolder.ifPresent(configsFolder1 -> WriteCommandAction.runWriteCommandAction(project, () -> {
            VirtualFile configsDirectoryVf = VfsUtil.findFile(Paths.get(configsFolder1), true);
            // TODO: Need to find out host and port.
            // TODO: Strategy: If the openapi defines it, then we use the localhost or 0.0.0.0 otherwise we use 8484.
            // TODO: Add success popup with the host where it is bound!
            ConfigProperties configProperties = new ConfigProperties(configId, title, "localhost", "8282");
            configProperties.put("openApiObject", configOpenApi);
            Template.OpenAPI.FLOW_CONFIG.create(project, configProperties, configsDirectoryVf, configFileName);
        }));
    }

    private String assetResource(String fileName) {
        if (StringUtils.isBlank(targetDirectory)) {
            return ModuleProperties.Assets.RESOURCE_DIRECTORY + File.separator + fileName;
        } else {
            return ModuleProperties.Assets.RESOURCE_DIRECTORY +
                    File.separator + targetDirectory + File.separator + fileName;
        }
    }

    public void register(String schemaId, String schemaAssetPath) {
        schemaIdAndPath.put(schemaId, schemaAssetPath);
    }

    public void registerRequestBody(String requestBodyId, RequestBodyObject requestBodyMediaType) {
        this.requestBodyIdAndData.put(requestBodyId, requestBodyMediaType);
    }

    public RequestBodyObject getRequestBodyById(String requestBodyId) {
        return this.requestBodyIdAndData.get(requestBodyId);
    }

    public Optional<String> assetFrom(String $ref) {
        for (Map.Entry<String,String> schemaIdAndPath : schemaIdAndPath.entrySet()) {
            String schemaId = schemaIdAndPath.getKey();
            if ($ref.endsWith(schemaId)) {
                return Optional.of(schemaIdAndPath.getValue());
            }
        }
        return Optional.empty();
    }

    private Module getImportModule() {
        return ModuleManager.getInstance(project).findModuleByName(importModuleName);
    }
}

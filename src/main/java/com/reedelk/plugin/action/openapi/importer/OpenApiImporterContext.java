package com.reedelk.plugin.action.openapi.importer;

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
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.nio.file.Paths;
import java.util.*;

import static com.reedelk.plugin.action.openapi.OpenApiUtils.SchemaFormat;

public class OpenApiImporterContext {

    private final String openApiFilePath;
    private final String importModuleName;
    private final Project project;
    private final String configId = UUID.randomUUID().toString();

    private Map<String,String> schemaIdAndPath = new HashMap<>();
    private Map<String, RequestBodyObject> requestBodyIdAndData = new HashMap<>();

    public OpenApiImporterContext(@NotNull Project project, String openAPIFilePath, String importModuleName) {
        this.project = project;
        this.openApiFilePath = openAPIFilePath;
        this.importModuleName = importModuleName;
    }

    public String getOpenApiFilePath() {
        return openApiFilePath;
    }

    public SchemaFormat getSchemaFormat() {
        return SchemaFormat.formatOf(openApiFilePath);
    }

    public String getConfigId() {
        return configId;
    }

    public Optional<String> createAsset(String schemaId, SchemaObject schemaObject, SchemaFormat schemaFormat) {
        if (schemaObject.getSchema() != null) {
            Map<String, Object> schemaData = schemaObject.getSchema().getSchemaData();
            Properties properties = new Properties();

            if (SchemaFormat.JSON.equals(schemaFormat)) {
                properties.put("data", new JSONObject(schemaData).toString(2));
            }

            if (SchemaFormat.YAML.equals(schemaFormat)) {
                properties.put("data", new Yaml().dump(schemaData));
            }


            Module module = getImportModule();

            String finalFileName = schemaId + "." + schemaFormat.getExtension();
            Optional<String> flowsDirectory = PluginModuleUtils.getAssetsDirectory(module);
            flowsDirectory.ifPresent(directory -> WriteCommandAction.runWriteCommandAction(project, () -> {
                VirtualFile flowsFolderVf = VfsUtil.findFile(Paths.get(directory), true);
                Template.OpenAPI.ASSET.create(project, properties, flowsFolderVf, finalFileName)
                        .ifPresent(virtualFile -> FileEditorManager.getInstance(project)
                                .openFile(virtualFile, false));
            }));

            return  Optional.of("assets/" + finalFileName);
        }
        return Optional.empty();
    }

    public String createAsset(String fileName, String data) {
        Module module = getImportModule();

        Properties properties = new Properties();
        properties.put("data", data);

        Optional<String> assetsDirectory = PluginModuleUtils.getAssetsDirectory(module);
        assetsDirectory.ifPresent(directory -> WriteCommandAction.runWriteCommandAction(project, () -> {
            VirtualFile assetsDirectoryVf = VfsUtil.findFile(Paths.get(directory), true);
            Template.OpenAPI.ASSET.create(project, properties, assetsDirectoryVf, fileName);
        }));

        return  "assets/" + fileName;
    }

    public void createFlow(String fileName, Properties properties) {
        Module module = getImportModule();

        Optional<String> flowsDirectory = PluginModuleUtils.getFlowsDirectory(module);
        flowsDirectory.ifPresent(directory -> WriteCommandAction.runWriteCommandAction(project, () -> {
            VirtualFile flowsFolderVf = VfsUtil.findFile(Paths.get(directory), true);
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
            ConfigProperties configProperties = new ConfigProperties(configId, title);
            configProperties.put("openApiObject", configOpenApi);
            Template.OpenAPI.FLOW_CONFIG.create(project, configProperties, configsDirectoryVf, configFileName);
        }));
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

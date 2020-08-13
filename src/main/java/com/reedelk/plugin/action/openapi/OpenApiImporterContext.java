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
import com.reedelk.plugin.exception.PluginException;
import com.reedelk.plugin.template.AssetProperties;
import com.reedelk.plugin.template.RestListenerOpenApiConfigProperties;
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

import static com.reedelk.plugin.template.Template.Buildable;
import static com.reedelk.plugin.template.Template.OpenAPI;

public class OpenApiImporterContext {

    private final String openApiFilePath;
    private final String importModuleName;
    private final String targetDirectory;
    private final Project project;
    private final String configId = UUID.randomUUID().toString();
    private final String apiFileUrl;

    private Map<String, String> schemaIdAndPath = new HashMap<>();
    private Map<String, RequestBodyObject> requestBodyIdAndData = new HashMap<>();

    public OpenApiImporterContext(@NotNull Project project, String openAPIFilePath, String importModuleName, String targetDirectory, String apiFileUrl) {
        this.project = project;
        this.apiFileUrl = apiFileUrl;
        this.targetDirectory = targetDirectory;
        this.openApiFilePath = openAPIFilePath;
        this.importModuleName = importModuleName;
    }

    public String getOpenApiFilePath() {
        return openApiFilePath;
    }

    public String getApiFileUrl() {
        return apiFileUrl;
    }

    // TODO: Can we rely on the extension for file url as well? perhaps the safest bet is to parse it.
    public OpenApiSchemaFormat getSchemaFormat() {
        if (StringUtils.isNotBlank(openApiFilePath)) {
            return OpenApiSchemaFormat.formatOf(openApiFilePath);
        } else {
            return OpenApiSchemaFormat.formatOf(apiFileUrl);
        }
    }

    public String getConfigId() {
        return configId;
    }

    public Optional<String> createAsset(String schemaId, SchemaObject schemaObject, OpenApiSchemaFormat schemaFormat) {
        if (schemaObject.getSchema() == null) return Optional.empty();


        Map<String, Object> schemaData = schemaObject.getSchema().getSchemaData();

        Properties properties;
        if (OpenApiSchemaFormat.JSON.equals(schemaFormat)) {
            String data = new JSONObject(schemaData).toString(2);
            properties = new AssetProperties(data);
        } else {
            // YAML
            String data = new Yaml().dump(schemaData);
            properties = new AssetProperties(data);
        }

        Module module = getImportModule();

        String finalFileName = schemaId + "." + schemaFormat.getExtension();
        Optional<String> assetsDirectory = PluginModuleUtils.getAssetsDirectory(module);
        assetsDirectory.ifPresent(directory ->
                createBuildable(OpenAPI.ASSET, properties, finalFileName, directory, false));
        return Optional.of(assetResource(finalFileName));
    }

    public String createAsset(String fileName, String data) {
        Module module = getImportModule();
        Properties properties = new AssetProperties(data);
        Optional<String> assetsDirectory = PluginModuleUtils.getAssetsDirectory(module);
        assetsDirectory.ifPresent(directory ->
                createBuildable(OpenAPI.ASSET, properties, fileName, directory, false));
        return assetResource(fileName);
    }

    public void createFlow(String fileName, Properties properties) {
        Module module = getImportModule();
        Optional<String> flowsDirectory = PluginModuleUtils.getFlowsDirectory(module);
        flowsDirectory.ifPresent(directory ->
                createBuildable(OpenAPI.FLOW_WITH_REST_LISTENER, properties, fileName, directory, true));
    }

    public void createRestListenerConfig(String configFileName, String configTitle, String configOpenApiObject, String host, int port) {
        Module module = getImportModule();
        PluginModuleUtils.getConfigsDirectory(module)
                .ifPresent(configsDirectory -> WriteCommandAction.runWriteCommandAction(project, () -> {
                    VirtualFile configsDirectoryVf = VfsUtil.findFile(Paths.get(configsDirectory), true);
                    RestListenerOpenApiConfigProperties properties =
                            new RestListenerOpenApiConfigProperties(configId, configTitle, host, port, configOpenApiObject);
                    OpenAPI.REST_LISTENER_CONFIG.create(project, properties, configsDirectoryVf, configFileName);
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
        for (Map.Entry<String, String> schemaIdAndPath : schemaIdAndPath.entrySet()) {
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

    private void createBuildable(Buildable buildable, Properties properties, String finalFileName, String directory, boolean openFile) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            Path targetDirectory = Paths.get(directory, this.targetDirectory);
            createDirectoryIfMissing(targetDirectory);
            VirtualFile flowsTargetDirectoryVf = VfsUtil.findFile(targetDirectory, true);
            buildable.create(project, properties, flowsTargetDirectoryVf, finalFileName)
                    .ifPresent(virtualFile -> {
                        if (openFile) {
                            FileEditorManager.getInstance(project)
                                    .openFile(virtualFile, false);
                        }
                    });
        });
    }

    private void createDirectoryIfMissing(Path targetDirectory) throws PluginException {
        try {
            VfsUtil.createDirectoryIfMissing(targetDirectory.toString());
        } catch (IOException e) {
            // TODO: Extract resources
            throw new PluginException("Could not create directory=[%s], cause: "+e.getMessage());
        }
    }
}

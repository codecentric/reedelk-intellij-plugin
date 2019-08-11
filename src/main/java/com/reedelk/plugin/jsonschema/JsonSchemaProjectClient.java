package com.reedelk.plugin.jsonschema;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.commons.ModuleUtils;
import com.reedelk.plugin.commons.ProjectFileContentProvider;
import org.everit.json.schema.loader.SchemaClient;
import org.everit.json.schema.loader.internal.DefaultSchemaClient;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class JsonSchemaProjectClient implements SchemaClient {

    private final SchemaClient fallbackClient;
    private final String parentFolder;
    private final String rootHostPath;
    private final Module module;

    public JsonSchemaProjectClient(@NotNull Module module,
                                   @NotNull String parentFolder,
                                   @NotNull String rootHostPath) {
        this.fallbackClient = new DefaultSchemaClient();
        this.parentFolder = parentFolder;
        this.rootHostPath = rootHostPath;
        this.module = module;
    }

    @Override
    public InputStream get(String url) {
        if (url.startsWith(rootHostPath)) {
            String relative = url.substring(rootHostPath.length());
            String finalFilePath = parentFolder + relative;
            return ProjectFileContentProvider.of(finalFilePath)
                    .map(content -> new ByteArrayInputStream(content.getBytes()))
                    .orElseGet(() -> new ByteArrayInputStream("{}".getBytes()));
        }

        return ModuleUtils.getResourcesFolder(module)
                .map(resourcesFolder -> loadFromResources(resourcesFolder, url))
                .orElseGet(() -> fallbackClient.get(url));
    }

    private InputStream loadFromResources(String resourcesFolder, String url) {
        String finalFilePath = resourcesFolder + url;
        return ProjectFileContentProvider.of(finalFilePath)
                .map(content -> new ByteArrayInputStream(content.getBytes()))
                .orElseGet(() -> new ByteArrayInputStream("{}".getBytes()));
    }
}

package com.esb.plugin.jsonschema;

import com.esb.plugin.commons.ModuleUtils;
import com.esb.plugin.editor.properties.widget.input.script.ProjectFileContentProvider;
import com.intellij.openapi.module.Module;
import org.everit.json.schema.loader.SchemaClient;
import org.everit.json.schema.loader.internal.DefaultSchemaClient;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class JsonSchemaProjectClient implements SchemaClient {

    private final ProjectFileContentProvider provider;
    private final SchemaClient fallbackClient;
    private final String parentFolder;
    private final String rootHostPath;
    private final Module module;

    JsonSchemaProjectClient(@NotNull Module module,
                            @NotNull String parentFolder,
                            @NotNull String rootHostPath,
                            @NotNull ProjectFileContentProvider provider) {
        this.fallbackClient = new DefaultSchemaClient();
        this.parentFolder = parentFolder;
        this.rootHostPath = rootHostPath;
        this.provider = provider;
        this.module = module;
    }

    @Override
    public InputStream get(String url) {
        if (url.startsWith(rootHostPath)) {
            String relative = url.substring(rootHostPath.length());
            String finalFilePath = parentFolder + relative;
            String content = provider.getContent(finalFilePath);
            return new ByteArrayInputStream(content.getBytes());
        }

        return ModuleUtils.getResourcesFolder(module)
                .map(resourcesFolder -> loadFromResources(resourcesFolder, url))
                .orElseGet(() -> fallbackClient.get(url));
    }

    private InputStream loadFromResources(String resourcesFolder, String url) {
        String finalFilePath = resourcesFolder + url;
        String content = provider.getContent(finalFilePath);
        return new ByteArrayInputStream(content.getBytes());
    }
}

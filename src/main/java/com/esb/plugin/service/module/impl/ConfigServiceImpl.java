package com.esb.plugin.service.module.impl;

import com.esb.internal.commons.FileExtension;
import com.esb.plugin.commons.ModuleUtils;
import com.esb.plugin.configuration.serializer.ConfigSerializer;
import com.esb.plugin.service.module.ConfigService;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ThrowableRunnable;
import org.json.JSONObject;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.esb.internal.commons.Preconditions.checkState;
import static java.util.stream.Collectors.toList;

public class ConfigServiceImpl implements ConfigService {

    private final Module module;

    public ConfigServiceImpl(Module module) {
        this.module = module;
    }

    @Override
    public List<ConfigMetadata> listConfigs(String fullyQualifiedName) {
        return listConfigs()
                .stream()
                .filter(configMetadata ->
                        configMetadata.getFullyQualifiedName().equals(fullyQualifiedName))
                .collect(toList());
    }

    @Override
    public void saveConfig(ConfigMetadata selectedMetadata) {
        VirtualFile file = VfsUtil.findFile(Paths.get(selectedMetadata.getConfigFile()), true);
        checkState(file != null, "Expected file not found");

        Document document = FileDocumentManager.getInstance().getDocument(file);
        checkState(document != null,
                String.format("Expected document for file %s to be found", selectedMetadata.getConfigFile()));

        String serializedConfig = ConfigSerializer.serialize(selectedMetadata);
        try {
            WriteCommandAction.writeCommandAction(module.getProject())
                    .run(() -> document.setText(serializedConfig));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @Override
    public void addConfig(ConfigMetadata newConfigMetadata) {
        // We must create the config folder inside resources..
        String serializedConfig = ConfigSerializer.serialize(newConfigMetadata);
        // Check if the file exists already. if it does, throw exception

        try {
            String configsFolder = ModuleUtils.getConfigsFolder(module);
            VirtualFile configDir = VfsUtil.createDirectoryIfMissing(configsFolder);
            WriteCommandAction.writeCommandAction(module.getProject())
                    .run((ThrowableRunnable<Throwable>) () -> {
                        VirtualFile childData = configDir.createChildData(null, newConfigMetadata.getFileName());
                        VfsUtil.saveText(childData, serializedConfig);
                    });

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    private List<ConfigMetadata> listConfigs() {
        List<ConfigMetadata> configs = new ArrayList<>();
        ModuleRootManager.getInstance(module).getFileIndex().iterateContent(fileOrDir -> {
            if (FileExtension.FLOW_CONFIG.value().equals(fileOrDir.getExtension())) {
                getMetadataFrom(fileOrDir).ifPresent(configs::add);
            }
            return true;
        });
        return configs;
    }

    private Optional<ConfigMetadata> getMetadataFrom(VirtualFile virtualFile) {
        try {
            Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
            if (document != null) {
                String json = document.getText();
                JSONObject configDefinition = new JSONObject(json);
                return Optional.of(new ExistingConfigMetadata(virtualFile, configDefinition));
            }
        } catch (Exception e) {
            // nothing to do.
        }
        return Optional.empty();
    }
}
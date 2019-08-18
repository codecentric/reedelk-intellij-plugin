package com.reedelk.plugin.service.module.impl;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ThrowableRunnable;
import com.reedelk.plugin.commons.ModuleUtils;
import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.configuration.Deserializer;
import com.reedelk.plugin.configuration.Serializer;
import com.reedelk.plugin.service.module.ConfigService;
import com.reedelk.runtime.commons.FileExtension;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.reedelk.runtime.commons.Preconditions.checkState;

public class ConfigServiceImpl implements ConfigService {

    private final Module module;

    public ConfigServiceImpl(Module module) {
        this.module = module;
    }

    @Override
    @NotNull
    public List<ConfigMetadata> listConfigsBy(@NotNull ComponentPropertyDescriptor configPropertyDescriptor) {
        List<ConfigMetadata> configs = new ArrayList<>();
        ModuleRootManager.getInstance(module).getFileIndex().iterateContent(fileOrDir -> {
            if (FileExtension.FLOW_CONFIG.value().equals(fileOrDir.getExtension())) {
                getConfigurationFrom(fileOrDir, configPropertyDescriptor).ifPresent(configs::add);
            }
            return true;
        });
        return configs;
    }

    @Override
    public void saveConfig(@NotNull ConfigMetadata updatedConfig) throws IOException {
        VirtualFile file = VfsUtil.findFile(Paths.get(updatedConfig.getConfigFile()), true);
        checkState(file != null, "Expected file not found");

        Document document = FileDocumentManager.getInstance().getDocument(file);
        checkState(document != null,
                String.format("Expected document for file %s to be found", updatedConfig.getConfigFile()));

        String serializedConfig = Serializer.serialize(updatedConfig);
        executeWriteCommand(() -> document.setText(serializedConfig));
    }

    @Override
    public void addConfig(@NotNull ConfigMetadata newConfig) throws IOException {
        // Serialize the config
        String serializedConfig = Serializer.serialize(newConfig);

        Optional<String> maybeConfigDirectory = getConfigDirectory();
        if (maybeConfigDirectory.isPresent()) {
            String configDir = maybeConfigDirectory.get();

            final VirtualFile configFile = VfsUtil.findFile(Paths.get(configDir, newConfig.getFileName()), true);
            if (configFile != null) {
                throw new IOException(String.format("A configuration file named %s exists already. " +
                        "Please use a different file name.", newConfig.getFileName()));
            }

            executeWriteCommand(() -> {
                // Write the serialized config
                VirtualFile directoryIfMissing = VfsUtil.createDirectoryIfMissing(configDir);
                VirtualFile childData = directoryIfMissing.createChildData(null, newConfig.getFileName());
                VfsUtil.saveText(childData, serializedConfig);
            });
        }
    }

    @Override
    public void removeConfig(@NotNull ConfigMetadata toBeRemovedConfig) throws IOException {
        VirtualFile file = VfsUtil.findFile(Paths.get(toBeRemovedConfig.getConfigFile()), true);
        if (file != null) {
            executeWriteCommand(() -> file.delete(null));
        }
    }

    private Optional<? extends ConfigMetadata> getConfigurationFrom(VirtualFile virtualFile, ComponentPropertyDescriptor componentPropertyDescriptor) {
        Optional<Document> maybeDocument = getDocumentFromFile(virtualFile);
        if (maybeDocument.isPresent()) {
            String json = maybeDocument.get().getText();
            return Deserializer.deserialize(json, componentPropertyDescriptor)
                    .map(dataHolder -> new ExistingConfigMetadata(virtualFile, dataHolder));
        }
        return Optional.empty();
    }

    private Optional<Document> getDocumentFromFile(VirtualFile virtualFile) {
        Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
        return Optional.ofNullable(document);
    }

    private void executeWriteCommand(ThrowableRunnable<IOException> runnable) throws IOException {
        WriteCommandAction
                .writeCommandAction(module.getProject())
                .run(runnable);
    }

    private Optional<String> getConfigDirectory() {
        return ModuleUtils.getConfigsFolder(module);
    }
}
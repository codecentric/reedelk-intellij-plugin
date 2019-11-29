package com.reedelk.plugin.service.module.impl;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ThrowableRunnable;
import com.intellij.util.messages.Topic;
import com.reedelk.plugin.commons.ModuleUtils;
import com.reedelk.plugin.component.deserializer.ConfigurationDeserializer;
import com.reedelk.plugin.component.domain.TypeObjectDescriptor;
import com.reedelk.plugin.component.serializer.ConfigurationSerializer;
import com.reedelk.plugin.executor.PluginExecutor;
import com.reedelk.plugin.service.module.ConfigService;
import com.reedelk.runtime.commons.FileExtension;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.reedelk.runtime.commons.Preconditions.checkState;

public class ConfigServiceImpl implements ConfigService {

    private final Module module;
    private final ConfigChangeListener publisher;

    public ConfigServiceImpl(Module module) {
        this.module = module;
        this.publisher = module.getMessageBus().syncPublisher(TOPIC_CONFIG_CHANGE);
    }

    @Override
    public void fetchConfigurationsBy(TypeObjectDescriptor typeObjectDescriptor) {
        ReadAction.nonBlocking(() -> {
            List<ConfigMetadata> configs = new ArrayList<>();
            ModuleRootManager.getInstance(module).getFileIndex().iterateContent(fileOrDir -> {
                if (FileExtension.FLOW_CONFIG.value().equals(fileOrDir.getExtension())) {
                    getConfigurationFrom(fileOrDir, typeObjectDescriptor).ifPresent(configs::add);
                }
                return true;
            });
            publisher.onConfigs(configs);
        }).submit(PluginExecutor.getInstance());
    }

    @Override
    public void saveConfig(@NotNull ConfigMetadata updatedConfig) {
        try {
            VirtualFile file = VfsUtil.findFile(Paths.get(updatedConfig.getConfigFile()), true);
            checkState(file != null, "Expected file not found");

            Document document = FileDocumentManager.getInstance().getDocument(file);
            checkState(document != null,
                    String.format("Expected document for file %s to be found", updatedConfig.getConfigFile()));

            String serializedConfig = ConfigurationSerializer.serialize(updatedConfig);
            executeWriteCommand(() -> document.setText(serializedConfig));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addConfig(@NotNull ConfigMetadata newConfig) {

        try {

            // TODO: What if the dierctory does not  exists!?? It should be created here!! Fixit
            Optional<String> maybeConfigDirectory = getConfigDirectory();
            if (maybeConfigDirectory.isPresent()) {
                String configDir = maybeConfigDirectory.get();

                final VirtualFile configFile = VfsUtil.findFile(Paths.get(configDir, newConfig.getFileName()), true);
                if (configFile != null) {
                    throw new IOException(String.format("A configuration file named %s exists already. " +
                            "Please use a different file name.", newConfig.getFileName()));
                }

                executeWriteCommand(() -> {

                    // Create the directory
                    VirtualFile directoryIfMissing = VfsUtil.createDirectoryIfMissing(configDir);
                    if (directoryIfMissing == null) {
                        throw new IOException(String.format("Could not create config directory=[%s] to store configuration named=[%s]", configDir, newConfig.getFileName()));
                    }
                    VirtualFile childData = directoryIfMissing.createChildData(null, newConfig.getFileName());

                    // Serialize the config
                    String serializedConfig = ConfigurationSerializer.serialize(newConfig);
                    VfsUtil.saveText(childData, serializedConfig);
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeConfig(@NotNull ConfigMetadata toBeRemovedConfig) {
        try {
            VirtualFile file = VfsUtil.findFile(Paths.get(toBeRemovedConfig.getConfigFile()), true);
            if (file != null) {
                executeWriteCommand(() -> file.delete(null));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ConfigChangeListener {
        default void onConfigs(Collection<ConfigMetadata> configurations) {}
        default void onAddSuccess(ConfigMetadata configuration) {}
        default void onAddError(Exception exception) {}
        default void onRemoveSuccess() {}
        default void onRemoveError(Exception exception) {}
    }

    public static final Topic<ConfigChangeListener> TOPIC_CONFIG_CHANGE =
            new Topic<>("Config Change", ConfigChangeListener.class);


    private Optional<? extends ConfigMetadata> getConfigurationFrom(VirtualFile virtualFile, TypeObjectDescriptor configTypeDescriptor) {
        Optional<Document> maybeDocument = getDocumentFromFile(virtualFile);
        if (maybeDocument.isPresent()) {
            String json = maybeDocument.get().getText();
            return ConfigurationDeserializer.deserialize(json, configTypeDescriptor)
                    .map(dataHolder -> new ExistingConfigMetadata(virtualFile, dataHolder, configTypeDescriptor));
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
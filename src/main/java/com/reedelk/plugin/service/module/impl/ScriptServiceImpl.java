package com.reedelk.plugin.service.module.impl;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.concurrency.SequentialTaskExecutor;
import com.intellij.util.messages.Topic;
import com.reedelk.plugin.commons.ModuleUtils;
import com.reedelk.plugin.service.module.ScriptService;
import com.reedelk.runtime.api.commons.ScriptUtils;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.commons.FileExtension;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

import static com.reedelk.runtime.commons.ModuleProperties.Script;

public class ScriptServiceImpl implements ScriptService {

    private static final ExecutorService executor = SequentialTaskExecutor.createSequentialApplicationPoolExecutor("Script Resource");

    public interface ScriptResourceChangeListener {
        default void onScriptResources(Collection<ScriptResource> scriptResources) {
        }

        default void onAddError(Exception exception, ScriptResource resource) {
        }

        default void onRemoveError(Exception exception, ScriptResource resource) {
        }
    }

    public static final Topic<ScriptResourceChangeListener> TOPIC_SCRIPT_RESOURCE =
            new Topic<>("Script Resource Change", ScriptResourceChangeListener.class);

    private final ScriptResourceChangeListener publisher;
    private final Module module;

    public ScriptServiceImpl(Module module) {
        this.module = module;
        this.publisher = module.getMessageBus().syncPublisher(TOPIC_SCRIPT_RESOURCE);
    }

    @Override
    public void fetchScriptResources() {

        String scriptsFolder = ModuleUtils.getResourcesFolder(module)
                .map(resourcesFolder -> resourcesFolder + Script.RESOURCE_DIRECTORY)
                .orElseThrow(() -> new IllegalStateException("The project must have a resource folder defined in the project."));

        // We access the index, therefore we must wait to access.
        ReadAction.nonBlocking(() -> {

            List<ScriptResource> scripts = new ArrayList<>();
            ModuleRootManager.getInstance(module).getFileIndex().iterateContent(fileOrDir -> {
                if (FileExtension.SCRIPT.value().equals(fileOrDir.getExtension())) {
                    if (fileOrDir.getPresentableUrl().startsWith(scriptsFolder)) {
                        // We keep the path from .../resource/scripts to the end.
                        // The script root is therefore /resource/scripts.
                        String substring = fileOrDir.getPresentableUrl().substring(scriptsFolder.length() + 1);
                        scripts.add(new ScriptResource(substring, fileOrDir.getNameWithoutExtension()));
                    }
                }
                return true;
            });

            publisher.onScriptResources(scripts);
        }).submit(executor);

    }

    @Override
    public void addScript(String scriptFileName) {
        if (ScriptUtils.isEmpty(scriptFileName)) {
            // Throw new exception here...
            throw new ESBException(String.format("Script cannot be empty %s", scriptFileName));
        }

        if (!scriptFileName.endsWith("." + FileExtension.SCRIPT.value())) {
            scriptFileName += "." + FileExtension.SCRIPT.value();
        }

        final String finalScriptFileName = scriptFileName;

        Optional<String> scriptsDirectory = ModuleUtils.getScriptsFolder(module);

        WriteCommandAction.runWriteCommandAction(module.getProject(), () -> {
            try {
                // Create the directory
                VirtualFile directoryIfMissing = VfsUtil.createDirectoryIfMissing(scriptsDirectory.get());
                if (directoryIfMissing == null) {
                    throw new IOException(String.format("Could not create scripts directory=[%s] to store script file named=[%s]", scriptsDirectory.get(), finalScriptFileName));
                }
                directoryIfMissing.createChildData(null, finalScriptFileName);
            } catch (IOException e) {
                publisher.onAddError(e, new ScriptResource(finalScriptFileName, finalScriptFileName));
            }
        });
    }

    @Override
    public void removeScript(String scriptFileName) {
        WriteCommandAction.runWriteCommandAction(module.getProject(), () ->
                ModuleUtils.getScriptsFolder(module).ifPresent(scriptsDirectory -> {
                    final VirtualFile file = VfsUtil.findFile(Paths.get(scriptsDirectory, scriptFileName), true);
                    if (file == null) {
                        publisher.onRemoveError(new Exception("asdf"), new ScriptResource(scriptFileName, scriptFileName));
                        return;
                    }
                    try {
                        file.delete(null);
                    } catch (IOException e) {
                        publisher.onRemoveError(e, new ScriptResource(scriptFileName, scriptFileName));
                    }
                }));
    }
}

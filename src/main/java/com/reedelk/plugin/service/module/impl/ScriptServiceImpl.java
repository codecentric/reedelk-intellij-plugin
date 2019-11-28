package com.reedelk.plugin.service.module.impl;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ThrowableRunnable;
import com.reedelk.plugin.commons.ModuleUtils;
import com.reedelk.plugin.service.module.ScriptService;
import com.reedelk.runtime.api.commons.ScriptUtils;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.commons.FileExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.reedelk.runtime.commons.ModuleProperties.Script;

public class ScriptServiceImpl implements ScriptService {

    private final Module module;

    public ScriptServiceImpl(Module module) {
        this.module = module;
    }

    @Override
    public List<ScriptResource> getScripts() {

        String scriptsFolder = ModuleUtils.getResourcesFolder(module)
                .map(resourcesFolder -> resourcesFolder + Script.RESOURCE_DIRECTORY)
                .orElseThrow(() -> new IllegalStateException("The project must have a resource folder defined in the project."));

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

        return scripts;
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

        try {
            WriteCommandAction
                    .writeCommandAction(module.getProject())
                    .run((ThrowableRunnable<? extends Throwable>) () -> {
                        try {
                            // Create the directory
                            VirtualFile directoryIfMissing = VfsUtil.createDirectoryIfMissing(scriptsDirectory.get());
                            if (directoryIfMissing == null) {
                                throw new IOException(String.format("Could not create scripts directory=[%s] to store script file named=[%s]", scriptsDirectory.get(), finalScriptFileName));
                            }
                            directoryIfMissing.createChildData(null, finalScriptFileName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}

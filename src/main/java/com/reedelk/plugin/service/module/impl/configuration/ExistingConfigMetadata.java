package com.reedelk.plugin.service.module.impl.configuration;

import com.intellij.openapi.vfs.VirtualFile;
import com.reedelk.module.descriptor.model.ComponentDataHolder;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import org.jetbrains.annotations.NotNull;

public class ExistingConfigMetadata extends ConfigMetadata {

    private VirtualFile file;

    public ExistingConfigMetadata(@NotNull VirtualFile file,
                                  @NotNull ComponentDataHolder dataHolder,
                                  @NotNull TypeObjectDescriptor configObjectDescriptor) {
        super(dataHolder, configObjectDescriptor);
        this.file = file;
    }

    @Override
    public String getConfigFile() {
        return file.getPath();
    }

    @Override
    public String getFileName() {
        return file.getName();
    }

    @Override
    public boolean isEditable() {
        return true;
    }

    @Override
    public boolean isRemovable() {
        return true;
    }
}

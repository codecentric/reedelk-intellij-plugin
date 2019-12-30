package com.reedelk.plugin.service.module.impl.configuration;

import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import org.jetbrains.annotations.NotNull;

public class NewConfigMetadata extends ConfigMetadata {

    private String fileName;

    public NewConfigMetadata(@NotNull String defaultFileName,
                             @NotNull TypeObjectDescriptor.TypeObject data,
                             @NotNull TypeObjectDescriptor typeObjectDescriptor) {
        super(data, typeObjectDescriptor);
        this.fileName = defaultFileName;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setFileName(String newFileName) {
        this.fileName = newFileName;
    }
}

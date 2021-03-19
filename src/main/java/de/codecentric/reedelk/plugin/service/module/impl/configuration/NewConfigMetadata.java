package de.codecentric.reedelk.plugin.service.module.impl.configuration;

import de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor;
import org.jetbrains.annotations.NotNull;

public class NewConfigMetadata extends ConfigMetadata {

    private String fileName;

    public NewConfigMetadata(@NotNull String defaultFileName,
                             @NotNull ObjectDescriptor.TypeObject data,
                             @NotNull ObjectDescriptor typeObjectDescriptor) {
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

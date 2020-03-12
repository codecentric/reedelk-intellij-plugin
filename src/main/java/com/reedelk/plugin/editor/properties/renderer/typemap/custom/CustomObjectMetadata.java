package com.reedelk.plugin.editor.properties.renderer.typemap.custom;

import com.reedelk.module.descriptor.model.ComponentDataHolder;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import org.jetbrains.annotations.NotNull;

public class CustomObjectMetadata {

    private final ComponentDataHolder data;
    private final TypeObjectDescriptor customObjectDescriptor;

    public CustomObjectMetadata(@NotNull ComponentDataHolder data,
                                @NotNull TypeObjectDescriptor customObjectDescriptor) {
        this.data = data;
        this.customObjectDescriptor = customObjectDescriptor;
    }
}

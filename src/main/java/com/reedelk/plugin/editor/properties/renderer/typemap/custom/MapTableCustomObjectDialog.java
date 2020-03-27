package com.reedelk.plugin.editor.properties.renderer.typemap.custom;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.ComponentDataHolder;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.editor.properties.commons.AbstractPropertiesDialog;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MapTableCustomObjectDialog extends AbstractPropertiesDialog {

    private final Module module;
    private final ComponentDataHolder componentDataHolder;
    private final TypeObjectDescriptor typeObjectDescriptor;

    public MapTableCustomObjectDialog(@NotNull Module module,
                                      @NotNull String title,
                                      @NotNull TypeObjectDescriptor typeObjectDescriptor,
                                      @NotNull ComponentDataHolder value) {
        super(module, title,"Save");
        setTitle(title);
        this.typeObjectDescriptor = typeObjectDescriptor;
        this.componentDataHolder = value;
        this.module = module;
        init();
    }

    @Override
    protected JComponent content() {
        return new MapTableCustomObjectPanel(module, componentDataHolder, typeObjectDescriptor);
    }
}

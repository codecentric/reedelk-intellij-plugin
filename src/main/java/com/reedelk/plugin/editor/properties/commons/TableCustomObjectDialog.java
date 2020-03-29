package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.ComponentDataHolder;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.awt.BorderLayout.CENTER;

public class TableCustomObjectDialog extends AbstractPropertiesDialog {

    private final Module module;
    private final ComponentDataHolder componentDataHolder;
    private final TypeObjectDescriptor typeObjectDescriptor;

    public TableCustomObjectDialog(@NotNull Module module,
                                   @NotNull String title,
                                   @NotNull TypeObjectDescriptor typeObjectDescriptor,
                                   @NotNull ComponentDataHolder value) {
        super(module, title, message("dialog.custom.properties.btn.save"));
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

    private static class MapTableCustomObjectPanel extends DisposablePanel {

        public MapTableCustomObjectPanel(Module module, ComponentDataHolder dataHolder, TypeObjectDescriptor objectDescriptor) {
            List<PropertyDescriptor> descriptors = objectDescriptor.getObjectProperties();

            String typeFullyQualifiedName = objectDescriptor.getTypeFullyQualifiedName();

            PropertiesPanelHolder propertiesPanel =
                    new PropertiesPanelHolder(module, typeFullyQualifiedName, dataHolder, descriptors);

            setLayout(new BorderLayout());
            add(ContainerFactory.pushTop(propertiesPanel), CENTER);
        }
    }
}

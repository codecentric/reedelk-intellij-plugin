package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.ComponentDataHolder;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.commons.InitValuesFiller;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.awt.BorderLayout.CENTER;

public class TableCustomObjectDialog extends AbstractPropertiesDialog {

    private final Module module;
    private final DialogType dialogType;
    private final ComponentDataHolder componentDataHolder;
    private final TypeObjectDescriptor typeObjectDescriptor;

    public TableCustomObjectDialog(@NotNull Module module,
                                   @NotNull String title,
                                   @NotNull TypeObjectDescriptor typeObjectDescriptor,
                                   @NotNull ComponentDataHolder value,
                                   @NotNull DialogType dialogType) {
        super(module, title, message("dialog.custom.properties.btn.save"));
        this.module = module;
        this.dialogType = dialogType;
        this.componentDataHolder = value;
        this.typeObjectDescriptor = typeObjectDescriptor;
        init();
    }

    @Override
    protected JComponent content() {
        return new MapTableCustomObjectPanel(module, componentDataHolder, typeObjectDescriptor, dialogType);
    }

    private static class MapTableCustomObjectPanel extends DisposablePanel {

        public MapTableCustomObjectPanel(@NotNull Module module,
                                         @NotNull ComponentDataHolder dataHolder,
                                         @NotNull TypeObjectDescriptor objectDescriptor,
                                         @NotNull DialogType dialogType) {
            List<PropertyDescriptor> descriptors = objectDescriptor.getObjectProperties();

            if (DialogType.NEW == dialogType) {
                // Fill Default Properties Values
                InitValuesFiller.fill(dataHolder, descriptors);
            }

            String typeFullyQualifiedName = objectDescriptor.getTypeFullyQualifiedName();

            PropertiesPanelHolder propertiesPanel =
                    new PropertiesPanelHolder(module, typeFullyQualifiedName, dataHolder, descriptors);

            setLayout(new BorderLayout());
            add(ContainerFactory.pushTop(propertiesPanel), CENTER);
        }
    }

    public enum DialogType {
        NEW,
        EDIT
    }
}

package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.component.ComponentDataHolder;
import com.reedelk.module.descriptor.model.property.ObjectDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.commons.InitValuesFiller;
import com.reedelk.plugin.editor.properties.context.ContainerContext;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static com.reedelk.plugin.message.ReedelkBundle.message;
import static java.awt.BorderLayout.CENTER;

public class DialogTableCustomObject extends DialogAbstractProperties {

    private final Module module;
    private final DialogType dialogType;
    private final ContainerContext context;
    private final ObjectDescriptor typeObjectDescriptor;
    private final ComponentDataHolder componentDataHolder;

    public DialogTableCustomObject(@NotNull Module module,
                                   @NotNull ContainerContext context,
                                   @NotNull String title,
                                   @NotNull ObjectDescriptor typeObjectDescriptor,
                                   @NotNull ComponentDataHolder value,
                                   @NotNull DialogType dialogType) {
        super(module, title, message("dialog.custom.properties.btn.done"));
        this.module = module;
        this.context = context;
        this.dialogType = dialogType;
        this.componentDataHolder = value;
        this.typeObjectDescriptor = typeObjectDescriptor;
        init();
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        return new Action[]{getOKAction()};
    }

    @Override
    protected JComponent content() {
        return new MapTableCustomObjectPanel(module, componentDataHolder, typeObjectDescriptor, dialogType, context);
    }

    private static class MapTableCustomObjectPanel extends DisposablePanel {

        public MapTableCustomObjectPanel(@NotNull Module module,
                                         @NotNull ComponentDataHolder dataHolder,
                                         @NotNull ObjectDescriptor objectDescriptor,
                                         @NotNull DialogType dialogType,
                                         @NotNull ContainerContext context) {
            List<PropertyDescriptor> descriptors = objectDescriptor.getObjectProperties();

            if (DialogType.NEW == dialogType) {
                // Fill Default Properties Values because we are creating a brand new object.
                // This is needed in the case of List with custom object. Because the new
                // Object gets created whenever we open the dialog with dialog type 'NEW'.
                InitValuesFiller.fill(dataHolder, descriptors);
            }

            PropertiesPanelHolder propertiesPanel = new PropertiesPanelHolder(module, context, dataHolder, descriptors);

            setLayout(new BorderLayout());
            add(ContainerFactory.pushTop(propertiesPanel), CENTER);
        }
    }

    public enum DialogType {
        NEW,
        EDIT
    }
}

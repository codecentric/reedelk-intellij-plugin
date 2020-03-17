package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.ComponentDataHolder;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeMapDescriptor;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposableTabbedPane;
import com.reedelk.plugin.editor.properties.renderer.typemap.custom.MapTableCustomColumnModel;
import com.reedelk.plugin.editor.properties.renderer.typemap.custom.MapTableCustomColumnModelFactory;
import com.reedelk.plugin.editor.properties.renderer.typemap.custom.MapTableCustomObjectDialog;
import com.reedelk.plugin.editor.properties.renderer.typemap.primitive.MapTableColumnModelFactory;
import com.reedelk.plugin.editor.properties.renderer.typemap.primitive.MapTableModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MapPropertyRenderer extends BaseMapPropertyRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor descriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        final TypeMapDescriptor propertyType = descriptor.getType();
        final JComponent content = isPrimitiveValueType(propertyType) ?
                createContent(module, propertyAccessor, context) :
                createCustomObjectContent(module, propertyType, propertyAccessor, context);

        DisposableTabbedPane tabbedPane = tabbedPaneFrom(descriptor, context, propertyType);
        tabbedPane.addTab(descriptor.getDisplayName(), content);
        return tabbedPane;
    }

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull PropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {

        addTabbedPaneToParent(parent, rendered, descriptor, context);
    }

    private JComponent createContent(@NotNull Module module,
                                     @NotNull PropertyAccessor propertyAccessor,
                                     @NotNull ContainerContext context) {
        MapTableModel tableModel = new MapTableModel(propertyAccessor);
        MapTableColumnModelFactory columnModel = new MapTableColumnModelFactory();
        return new MapPropertyTabContainer(module.getProject(), tableModel, columnModel);
    }

    protected JComponent createCustomObjectContent(@NotNull Module module,
                                                   @NotNull TypeMapDescriptor descriptor,
                                                   @NotNull PropertyAccessor propertyAccessor,
                                                   @NotNull ContainerContext context) {

        TypeObjectDescriptor typeObjectDescriptor = (TypeObjectDescriptor) descriptor.getValueType();

        MapTableCustomColumnModel tableModel = new MapTableCustomColumnModel(propertyAccessor);

        MapTableCustomColumnModelFactory.ActionHandler action = value -> {
            MapTableCustomObjectDialog dialog = new MapTableCustomObjectDialog(module, "Test", typeObjectDescriptor, (ComponentDataHolder) value);
            dialog.showAndGet();
        };

        MapTableCustomColumnModelFactory columnModel = new MapTableCustomColumnModelFactory(action);
        return new MapPropertyTabContainer(module.getProject(), tableModel, columnModel);
    }

    private boolean isPrimitiveValueType(TypeMapDescriptor propertyType) {
        return !(propertyType.getValueType() instanceof TypeObjectDescriptor);
    }
}

package com.reedelk.plugin.editor.properties.renderer.typemap.dynamic;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeMapDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposableTabbedPane;
import com.reedelk.plugin.editor.properties.commons.DisposableTableModel;
import com.reedelk.plugin.editor.properties.renderer.typemap.BaseMapPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typemap.MapTableContainer;
import com.reedelk.plugin.editor.properties.renderer.typemap.MapTableTabContainer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Optional;
import java.util.function.Function;

public class DynamicMapPropertyRenderer extends BaseMapPropertyRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        final String propertyDisplayName = propertyDescriptor.getDisplayName();
        final TypeMapDescriptor propertyType = propertyDescriptor.getType();

        return Optional.ofNullable(propertyType.getTabGroup()).map((Function<String, JComponent>) tabGroupName -> {
            // The table must fit into a Table Tab Group.
            DisposableTableModel tableModel = new DynamicMapTableModel(propertyAccessor);
            DynamicMapTableColumnModelFactory columnModelFactory = new DynamicMapTableColumnModelFactory(module, propertyType, context);
            final JComponent content = new MapTableTabContainer(module, tableModel, columnModelFactory);
            final DisposableTabbedPane tabbedPane = tabbedPaneFrom(propertyDescriptor, context, propertyType);
            tabbedPane.addTab(propertyDisplayName, content);
            return tabbedPane;

        }).orElseGet(() -> {
            // Single table
            final DisposableTableModel tableModel = new DynamicMapTableModel(propertyAccessor);
            final DynamicMapTableColumnModelFactory columnModelFactory = new DynamicMapTableColumnModelFactory(module, propertyType, context);
            return new MapTableContainer(propertyDescriptor, module, tableModel, columnModelFactory);
        });
    }

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull PropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {
        addTabbedPaneToParent(parent, rendered, descriptor, context);
    }
}

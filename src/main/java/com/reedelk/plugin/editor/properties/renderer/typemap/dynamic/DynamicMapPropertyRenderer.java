package com.reedelk.plugin.editor.properties.renderer.typemap.dynamic;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.property.MapDescriptor;
import com.reedelk.module.descriptor.model.property.PropertyDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposableTabbedPane;
import com.reedelk.plugin.editor.properties.commons.DisposableTableModel;
import com.reedelk.plugin.editor.properties.commons.TabLabelHorizontal;
import com.reedelk.plugin.editor.properties.renderer.AbstractCollectionAwarePropertyTypeRenderer;
import com.reedelk.plugin.editor.properties.renderer.typemap.MapTableContainer;
import com.reedelk.plugin.editor.properties.renderer.typemap.MapTableTabContainer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

public class DynamicMapPropertyRenderer extends AbstractCollectionAwarePropertyTypeRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        final String propertyDisplayName = propertyDescriptor.getDisplayName();
        final MapDescriptor propertyType = propertyDescriptor.getType();

        return ofNullable(propertyType.getTabGroup())
                .map((Function<String, JComponent>) tabGroupName -> {
                    // The table must fit into a Table Tab Group.
                    DisposableTableModel tableModel = new DynamicMapTableModel(propertyAccessor);
                    DynamicMapTableColumnModelFactory columnModelFactory = new DynamicMapTableColumnModelFactory(module, propertyType, context);
                    final JComponent content = new MapTableTabContainer(module, tableModel, columnModelFactory);
                    final DisposableTabbedPane tabbedPane = tabbedPaneFrom(propertyDescriptor, context);
                    tabbedPane.addTab(propertyDisplayName, content);
                    tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, new TabLabelHorizontal(propertyDisplayName));
                    return tabbedPane;

                }).orElseGet(() -> {
                    // Single table
                    final DisposableTableModel tableModel = new DynamicMapTableModel(propertyAccessor);
                    final DynamicMapTableColumnModelFactory columnModelFactory =
                            new DynamicMapTableColumnModelFactory(module, propertyType, context);
                    return new MapTableContainer(module, tableModel, columnModelFactory);
                });
    }
}

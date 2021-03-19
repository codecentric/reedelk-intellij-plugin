package de.codecentric.reedelk.plugin.editor.properties.renderer.typemap.dynamic;

import de.codecentric.reedelk.plugin.editor.properties.commons.DisposableTabbedPane;
import de.codecentric.reedelk.plugin.editor.properties.commons.DisposableTableModel;
import de.codecentric.reedelk.plugin.editor.properties.commons.TabLabelHorizontal;
import de.codecentric.reedelk.plugin.editor.properties.context.ContainerContext;
import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;
import de.codecentric.reedelk.plugin.editor.properties.renderer.AbstractCollectionAwarePropertyTypeRenderer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typemap.MapTableContainer;
import de.codecentric.reedelk.plugin.editor.properties.renderer.typemap.MapTableTabContainer;
import com.intellij.openapi.module.Module;
import de.codecentric.reedelk.module.descriptor.model.property.MapDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

public class DynamicMapPropertyRenderer extends AbstractCollectionAwarePropertyTypeRenderer {

    @NotNull
    @Override
    public RenderedComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        MapDescriptor propertyType = propertyDescriptor.getType();
        String propertyName = propertyDescriptor.getName();
        String propertyDisplayName = propertyDescriptor.getDisplayName();
        String componentPropertyPath = context.getPropertyPath(propertyName);

        JComponent component = ofNullable(propertyType.getTabGroup())

                .map((Function<String, JComponent>) tabGroupName -> {
                    // The table must fit into a Table Tab Group.
                    DisposableTableModel tableModel = new DynamicMapTableModel(propertyAccessor);
                    DynamicMapTableColumnModelFactory columnModelFactory = new DynamicMapTableColumnModelFactory(module, propertyType, componentPropertyPath, context);
                    JComponent content = new MapTableTabContainer(module, tableModel, columnModelFactory);
                    DisposableTabbedPane tabbedPane = tabbedPaneFrom(propertyDescriptor, context);
                    tabbedPane.addTab(propertyDisplayName, content);
                    tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, new TabLabelHorizontal(propertyDisplayName));
                    return tabbedPane;

                }).orElseGet(() -> {
                    // Single table
                    DisposableTableModel tableModel = new DynamicMapTableModel(propertyAccessor);
                    DynamicMapTableColumnModelFactory columnModelFactory = new DynamicMapTableColumnModelFactory(module, propertyType, componentPropertyPath, context);
                    return new MapTableContainer(module, tableModel, columnModelFactory);
                });

        return RenderedComponent.create(component);
    }
}

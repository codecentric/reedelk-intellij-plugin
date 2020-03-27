package com.reedelk.plugin.editor.properties.renderer.typemap.dynamic;

import com.intellij.openapi.module.Module;
import com.intellij.util.ui.JBUI;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeMapDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.*;
import com.reedelk.plugin.editor.properties.renderer.typemap.BaseMapPropertyRenderer;
import com.reedelk.plugin.editor.properties.renderer.typemap.MapTableContainer;
import com.reedelk.plugin.editor.properties.renderer.typemap.MapTableTabContainer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Optional.ofNullable;

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

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull PropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {

        final TypeMapDescriptor propertyType = descriptor.getType();
        boolean isTabGroupPresent = ofNullable(propertyType.getTabGroup()).isPresent();

        if (isTabGroupPresent) {
            super.addToParent(parent, rendered, descriptor, context);
        } else {
            PropertyTitleLabel propertyTitleLabel = new PropertyTitleLabel(descriptor);
            propertyTitleLabel.setBorder(JBUI.Borders.emptyTop(12));
            FormBuilder.get()
                    .addLabelTop(propertyTitleLabel, parent)
                    .addLastField(rendered, parent);

            JComponentHolder holder = new JComponentHolder(rendered);
            context.addComponent(holder);
        }
    }
}

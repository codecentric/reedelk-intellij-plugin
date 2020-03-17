package com.reedelk.plugin.editor.properties.renderer.typemap.dynamic;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeMapDescriptor;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposableTabbedPane;
import com.reedelk.plugin.editor.properties.commons.DisposableTableModel;
import com.reedelk.plugin.editor.properties.renderer.typemap.BaseMapPropertyRenderer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class DynamicMapPropertyRenderer extends BaseMapPropertyRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor propertyDescriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        final TypeMapDescriptor propertyType = propertyDescriptor.getType();
        final DisposableTabbedPane tabbedPane = tabbedPaneFrom(propertyDescriptor, context, propertyType);
        final JComponent content = createContent(module, propertyAccessor, context);
        tabbedPane.addTab(propertyDescriptor.getDisplayName(), content);

        return tabbedPane;
    }

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull PropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {
        addTabbedPaneToParent(parent, rendered, descriptor, context);
    }

    protected JComponent createContent(@NotNull Module module,
                                       @NotNull PropertyAccessor propertyAccessor,
                                       @NotNull ContainerContext context) {
        DisposableTableModel tableModel = new DynamicMapTableModel(propertyAccessor);
        return new DynamicMapPropertyTabContainer(module, tableModel, context);
    }
}

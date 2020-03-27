package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeMapDescriptor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposableTabbedPane;
import com.reedelk.plugin.editor.properties.commons.FormBuilder;
import com.reedelk.plugin.editor.properties.commons.JComponentHolder;
import com.reedelk.plugin.editor.properties.renderer.AbstractPropertyTypeRenderer;
import com.reedelk.runtime.api.annotation.TabGroup;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.Optional;

public abstract class BaseMapPropertyRenderer extends AbstractPropertyTypeRenderer {

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull PropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {

        // If exists a group tabbed pane, then we don't add it to the parent because
        // it has been already added to the tabbed pane above in the render method.
        Optional<DisposableTabbedPane> groupTabbedPane = getGroupTabbedPane(descriptor, context);
        if (groupTabbedPane.isPresent()) {
            return;
        }

        // Add the component to the parent container.
        FormBuilder.get().addFullWidthAndHeight(rendered, parent);

        JComponentHolder holder = new JComponentHolder(rendered);

        // Add the component to the container context.
        TypeMapDescriptor propertyType = descriptor.getType();
        Optional.ofNullable(propertyType.getTabGroup())
                .ifPresent(group -> holder.addMetadata(TabGroup.class.getName(), group));

        context.addComponent(holder);
    }
}

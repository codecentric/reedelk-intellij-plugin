package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeMapDescriptor;
import com.reedelk.plugin.commons.VectorUtils;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposableTabbedPane;
import com.reedelk.runtime.converter.DeserializerConverter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

public class MapPropertyRenderer extends BaseMapPropertyRenderer {

    @NotNull
    @Override
    public JComponent render(@NotNull Module module,
                             @NotNull PropertyDescriptor descriptor,
                             @NotNull PropertyAccessor propertyAccessor,
                             @NotNull ContainerContext context) {

        final TypeMapDescriptor propertyType = descriptor.getType();

        if (isPrimitiveValueType(propertyType)) {
            DisposableTabbedPane tabbedPane = tabbedPaneFrom(descriptor, context, propertyType);
            JComponent content = createContent(module, propertyAccessor, context);
            tabbedPane.addTab(descriptor.getDisplayName(), content);
            return tabbedPane;
        } else {
            return new MapPropertyWithCustomObjectContainer();
        }
    }

    @Override
    public void addToParent(@NotNull JComponent parent,
                            @NotNull JComponent rendered,
                            @NotNull PropertyDescriptor descriptor,
                            @NotNull ContainerContext context) {

        if (isPrimitiveValueType(descriptor.getType())) {
            addTabbedPaneToParent(parent, rendered, descriptor, context);
        } else {
            super.addToParent(parent, rendered, descriptor, context);
        }
    }

    @SuppressWarnings("unchecked")
    protected JComponent createContent(Module module, PropertyAccessor propertyAccessor, @NotNull ContainerContext context) {
        MapTableModel tableModel = new MapTableModel(vectors -> {
            // Data Model Update
            Map<String, String> updated = new LinkedHashMap<>();
            vectors.forEach(vector -> {
                String key = VectorUtils.getOrEmptyIfNull((Vector<String>) vector, 0); // 0 is the key
                String value = VectorUtils.getOrEmptyIfNull((Vector<String>) vector, 1); // 1 is the value
                updated.put(key, value);
            });
            propertyAccessor.set(updated);
        });

        // Data Model Initialize
        Map<String, String> map = propertyAccessor.get();
        if (map != null) {
            map.forEach((key, value) -> tableModel.addRow(new Object[]{key, value}));
        }

        // Return the content
        return new MapPropertyTabContainer(module, tableModel);
    }


    private boolean isPrimitiveValueType(TypeMapDescriptor propertyType) {
        final String mapValueType = propertyType.getValueFullyQualifiedName();
        return DeserializerConverter.getInstance().isPrimitive(mapValueType);
    }
}

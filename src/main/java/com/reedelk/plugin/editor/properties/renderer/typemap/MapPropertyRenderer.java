package com.reedelk.plugin.editor.properties.renderer.typemap;

import com.intellij.openapi.module.Module;
import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeMapDescriptor;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.commons.VectorUtils;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.ContainerContext;
import com.reedelk.plugin.editor.properties.commons.DisposableTabbedPane;
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
        final JComponent content = isPrimitiveValueType(propertyType) ?
                createContent(module, propertyAccessor, context) :
                createCustomObjectContent(module, propertyAccessor, context);

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

    @SuppressWarnings("unchecked")
    private JComponent createContent(Module module, PropertyAccessor propertyAccessor, @NotNull ContainerContext context) {
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

        MapTableColumnModel columnModel = new MapTableColumnModel();
        return new MapPropertyTabContainer(module.getProject(), tableModel, columnModel);
    }

    @SuppressWarnings("unchecked")
    protected JComponent createCustomObjectContent(Module module, PropertyAccessor propertyAccessor, @NotNull ContainerContext context) {
        Map<String,String> data = propertyAccessor.get();
        MapTableModel tableModel = new MapTableModel(vectors -> {
            // Data Model Update
            Map<String, Object> updated = new LinkedHashMap<>();
            vectors.forEach(vector -> {
                String key = VectorUtils.getOrEmptyIfNull((Vector<String>) vector, 0); // 0 is the key
                Object value = VectorUtils.getOrEmptyIfNull((Vector<String>) vector, 1); // 1 is the value
                updated.put(key, value);
            });
            propertyAccessor.set(updated);
        });

        // Data Model Initialize
        data.forEach((key, value) -> tableModel.addRow(new Object[] { key, value }));

        MapTableCustomColumnModel.ActionHandler action = new MapTableCustomColumnModel.ActionHandler() {
            @Override
            public void onClick(Object value) {
                System.out.println("hello");

            }
        };
        MapTableCustomColumnModel columnModel = new MapTableCustomColumnModel(action);
        return new MapPropertyTabContainer(module.getProject(), tableModel, columnModel);
    }

    private boolean isPrimitiveValueType(TypeMapDescriptor propertyType) {
        return !(propertyType.getValueType() instanceof TypeObjectDescriptor);
    }
}

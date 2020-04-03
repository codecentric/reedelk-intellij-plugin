package com.reedelk.plugin.editor.properties.renderer.typemap.custom;

import com.reedelk.module.descriptor.model.PropertyDescriptor;
import com.reedelk.module.descriptor.model.TypeObjectDescriptor;
import com.reedelk.plugin.commons.InitValuesFiller;
import com.reedelk.plugin.commons.VectorUtils;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.TableModelDefaultAbstract;
import com.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import static com.reedelk.module.descriptor.model.TypeObjectDescriptor.TypeObject;

public class MapTableCustomModel extends TableModelDefaultAbstract {

    private final transient PropertyAccessor propertyAccessor;
    private final TypeObjectDescriptor typeObjectDescriptor;

    public MapTableCustomModel(@NotNull PropertyAccessor propertyAccessor,
                               @NotNull TypeObjectDescriptor typeObjectDescriptor) {
        this.typeObjectDescriptor = typeObjectDescriptor;
        this.propertyAccessor = propertyAccessor;
        Map<String, TypeObject> data = propertyAccessor.get();
        if (data != null) {
            data.forEach((key, value) -> addRow(new Object[]{key, value}));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onUpdate(Vector<?> data) {
        Map<String, Object> updated = new LinkedHashMap<>();
        data.forEach(vector -> {
            String key = VectorUtils.getOrEmptyIfNull((Vector<String>) vector, 0); // 0 is the key
            TypeObject value = ((Vector<TypeObject>)vector).get(1); // 1 is the value
            updated.put(key, value);
        });
        propertyAccessor.set(updated);
    }

    @Override
    public Object[] createRow() {
        // When we create a new custom object we must fill it with the initial values
        // configured using @InitValue annotation on each property. This is used
        // when we have a Map with custom value type.
        TypeObject newTypeObjectInstance = TypeObjectDescriptor.newInstance();
        List<PropertyDescriptor> descriptors = typeObjectDescriptor.getObjectProperties();
        InitValuesFiller.fill(newTypeObjectInstance, descriptors);

        return new Object[]{ StringUtils.EMPTY, newTypeObjectInstance };
    }
}

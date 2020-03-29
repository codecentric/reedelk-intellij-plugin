package com.reedelk.plugin.editor.properties.renderer.typemap.custom;

import com.reedelk.plugin.commons.VectorUtils;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.commons.TableModelDefaultAbstract;
import com.reedelk.runtime.api.commons.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

import static com.reedelk.module.descriptor.model.TypeObjectDescriptor.TypeObject;
import static com.reedelk.module.descriptor.model.TypeObjectDescriptor.newInstance;

public class MapTableCustomModel extends TableModelDefaultAbstract {

    private final transient PropertyAccessor propertyAccessor;

    public MapTableCustomModel(PropertyAccessor propertyAccessor) {
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
        return new Object[]{
                StringUtils.EMPTY,
                newInstance()};
    }
}

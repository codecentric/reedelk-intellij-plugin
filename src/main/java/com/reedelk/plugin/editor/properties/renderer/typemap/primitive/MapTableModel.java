package com.reedelk.plugin.editor.properties.renderer.typemap.primitive;

import com.reedelk.plugin.commons.VectorUtils;
import com.reedelk.plugin.editor.properties.accessor.PropertyAccessor;
import com.reedelk.plugin.editor.properties.renderer.typemap.AbstractMapTableModel;
import com.reedelk.runtime.api.commons.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

public class MapTableModel extends AbstractMapTableModel {

    private final PropertyAccessor propertyAccessor;

    public MapTableModel(PropertyAccessor propertyAccessor) {
        super();
        this.propertyAccessor = propertyAccessor;

        // Data Model Initialize
        Map<String, String> map = propertyAccessor.get();
        if (map != null) {
            map.forEach((key, value) -> addRow(new Object[]{key, value}));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onUpdate(Vector<?> data) {
        Map<String, String> updated = new LinkedHashMap<>();
        data.forEach(vector -> {
            String key = VectorUtils.getOrEmptyIfNull((Vector<String>) vector, 0); // 0 is the key
            String value = VectorUtils.getOrEmptyIfNull((Vector<String>) vector, 1); // 1 is the value
            updated.put(key, value);
        });
        propertyAccessor.set(updated);
    }

    @Override
    public Object[] createRow() {
        return new Object[]{StringUtils.EMPTY, StringUtils.EMPTY};
    }
}

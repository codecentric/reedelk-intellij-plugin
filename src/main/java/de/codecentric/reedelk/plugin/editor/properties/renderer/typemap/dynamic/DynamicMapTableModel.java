package de.codecentric.reedelk.plugin.editor.properties.renderer.typemap.dynamic;

import de.codecentric.reedelk.plugin.editor.properties.commons.TableModelDefaultAbstract;
import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;
import de.codecentric.reedelk.plugin.commons.VectorUtils;
import de.codecentric.reedelk.runtime.api.commons.ScriptUtils;
import de.codecentric.reedelk.runtime.api.commons.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

public class DynamicMapTableModel extends TableModelDefaultAbstract {

    private final transient PropertyAccessor propertyAccessor;

    public DynamicMapTableModel(PropertyAccessor propertyAccessor) {
        super();
        this.propertyAccessor = propertyAccessor;
        // Init
        // Data Model Initialize
        Map<String, String> map = propertyAccessor.get();
        if (map != null) {
            map.forEach((key, value) -> addRow(new Object[]{key, ScriptUtils.unwrap(value)}));
        }
    }

    @Override
    public Object[] createRow() {
        return new Object[]{ StringUtils.EMPTY, StringUtils.EMPTY };
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onUpdate(Vector<?> data) {
        // Model Update
        Map<String, String> updated = new LinkedHashMap<>();
        data.forEach(vector -> {
            String key = VectorUtils.getOrEmptyIfNull((Vector<String>) vector, 0); // 0 is the key
            String value = ScriptUtils.asScript(VectorUtils.getOrEmptyIfNull((Vector<String>) vector, 1)); // 1 is the value
            updated.put(key, value);
        });
        propertyAccessor.set(updated);
    }
}

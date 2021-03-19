package de.codecentric.reedelk.plugin.editor.properties.renderer.typemap.custom;

import de.codecentric.reedelk.plugin.editor.properties.commons.TableModelDefaultAbstract;
import de.codecentric.reedelk.plugin.editor.properties.context.PropertyAccessor;
import de.codecentric.reedelk.module.descriptor.model.property.ObjectDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import de.codecentric.reedelk.plugin.commons.InitValuesFiller;
import de.codecentric.reedelk.plugin.commons.VectorUtils;
import de.codecentric.reedelk.runtime.api.commons.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class MapTableCustomModel extends TableModelDefaultAbstract {

    private final transient PropertyAccessor propertyAccessor;
    private final ObjectDescriptor typeObjectDescriptor;

    public MapTableCustomModel(@NotNull PropertyAccessor propertyAccessor,
                               @NotNull ObjectDescriptor typeObjectDescriptor) {
        this.typeObjectDescriptor = typeObjectDescriptor;
        this.propertyAccessor = propertyAccessor;
        Map<String, ObjectDescriptor.TypeObject> data = propertyAccessor.get();
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
            ObjectDescriptor.TypeObject value = ((Vector<ObjectDescriptor.TypeObject>)vector).get(1); // 1 is the value
            updated.put(key, value);
        });
        propertyAccessor.set(updated);
    }

    @Override
    public Object[] createRow() {
        // When we create a new custom object we must fill it with the initial values
        // configured using @InitValue annotation on each property. This is used
        // when we have a Map with custom value type.
        ObjectDescriptor.TypeObject newTypeObjectInstance = ObjectDescriptor.newInstance();
        List<PropertyDescriptor> descriptors = typeObjectDescriptor.getObjectProperties();
        InitValuesFiller.fill(newTypeObjectInstance, descriptors);

        return new Object[]{ StringUtils.EMPTY, newTypeObjectInstance };
    }
}

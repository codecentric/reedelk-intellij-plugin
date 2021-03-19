package de.codecentric.reedelk.plugin.editor.properties.context;

import de.codecentric.reedelk.plugin.component.type.unknown.UnknownPropertyType;
import de.codecentric.reedelk.plugin.exception.PluginException;
import de.codecentric.reedelk.plugin.graph.FlowSnapshot;
import de.codecentric.reedelk.module.descriptor.model.component.ComponentDataHolder;
import de.codecentric.reedelk.module.descriptor.model.property.*;
import de.codecentric.reedelk.runtime.api.commons.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static de.codecentric.reedelk.runtime.api.commons.Preconditions.checkState;

class PropertyAccessorFactory {

    private String propertyName;
    private FlowSnapshot snapshot;
    private PropertyTypeDescriptor typeDescriptor;
    private ComponentDataHolder dataHolder;

    private static final Map<Class<? extends PropertyTypeDescriptor>, Class<? extends PropertyAccessor>> ACCESSOR_MAP;

    static {
        Map<Class<? extends PropertyTypeDescriptor>, Class<? extends PropertyAccessor>> tmp = new HashMap<>();
        tmp.put(MapDescriptor.class, DefaultPropertyAccessor.class);
        tmp.put(ListDescriptor.class, DefaultPropertyAccessor.class);
        tmp.put(EnumDescriptor.class, DefaultPropertyAccessor.class);
        tmp.put(UnknownPropertyType.class, DefaultPropertyAccessor.class);
        tmp.put(ComboDescriptor.class, DefaultPropertyAccessor.class);
        tmp.put(ObjectDescriptor.class, DefaultPropertyAccessor.class);
        tmp.put(ScriptDescriptor.class, DefaultPropertyAccessor.class);
        tmp.put(ResourceTextDescriptor.class, DefaultPropertyAccessor.class);
        tmp.put(ResourceBinaryDescriptor.class, DefaultPropertyAccessor.class);
        tmp.put(PasswordDescriptor.class, DefaultPropertyAccessor.class);
        tmp.put(PrimitiveDescriptor.class, DefaultPropertyAccessor.class);
        tmp.put(DynamicMapDescriptor.class, DefaultPropertyAccessor.class);
        tmp.put(DynamicValueDescriptor.class, DefaultPropertyAccessor.class);
        ACCESSOR_MAP = tmp;
    }

    private PropertyAccessorFactory() {
    }

    static PropertyAccessorFactory get() {
        return new PropertyAccessorFactory();
    }

    public PropertyAccessorFactory typeDescriptor(PropertyTypeDescriptor typeDescriptor) {
        this.typeDescriptor = typeDescriptor;
        return this;
    }

    public PropertyAccessorFactory dataHolder(ComponentDataHolder dataHolder) {
        this.dataHolder = dataHolder;
        return this;
    }

    public PropertyAccessorFactory propertyName(String propertyName) {
        this.propertyName = propertyName;
        return this;
    }

    public PropertyAccessorFactory snapshot(FlowSnapshot snapshot) {
        this.snapshot = snapshot;
        return this;
    }

    public PropertyAccessor build() {
        checkState(StringUtils.isNotBlank(propertyName), "property name must not be empty");
        checkState(typeDescriptor != null, "type descriptor must not be null");
        checkState(dataHolder != null, "data holder must not be null");
        checkState(ACCESSOR_MAP.containsKey(typeDescriptor.getClass()),
                String.format("accessor for type %s not defined", typeDescriptor.getType()));

        Class<? extends PropertyAccessor> accessorClazz = ACCESSOR_MAP.get(typeDescriptor.getClass());
        return instantiate(accessorClazz);
    }

    private PropertyAccessor instantiate(Class<? extends PropertyAccessor> accessorClazz) {
        try {
            if (snapshot != null) {
                return accessorClazz
                        .getDeclaredConstructor(String.class, ComponentDataHolder.class, FlowSnapshot.class)
                        .newInstance(propertyName, dataHolder, snapshot);
            } else {
                return accessorClazz
                        .getDeclaredConstructor(String.class, ComponentDataHolder.class)
                        .newInstance(propertyName, dataHolder);
            }
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException exception) {
            throw new PluginException("Could not instantiate accessor class=" + accessorClazz.getName(), exception);
        }
    }
}

package com.reedelk.plugin.editor.properties.accessor;

import com.reedelk.plugin.component.domain.*;
import com.reedelk.plugin.component.type.unknown.UnknownPropertyType;
import com.reedelk.plugin.graph.FlowSnapshot;
import com.reedelk.runtime.api.exception.ESBException;
import com.reedelk.runtime.commons.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static com.reedelk.runtime.commons.Preconditions.checkState;

public class PropertyAccessorFactory {

    private String propertyName;
    private FlowSnapshot snapshot;
    private TypeDescriptor typeDescriptor;
    private ComponentDataHolder dataHolder;

    private static final Map<Class<? extends TypeDescriptor>, Class<? extends PropertyAccessor>> ACCESSOR_MAP;

    static {
        Map<Class<? extends TypeDescriptor>, Class<? extends PropertyAccessor>> tmp = new HashMap<>();
        tmp.put(TypeMapDescriptor.class, DefaultPropertyAccessor.class);
        tmp.put(TypeEnumDescriptor.class, DefaultPropertyAccessor.class);
        tmp.put(TypeFileDescriptor.class, DefaultPropertyAccessor.class);
        tmp.put(UnknownPropertyType.class, DefaultPropertyAccessor.class);
        tmp.put(TypeObjectDescriptor.class, DefaultPropertyAccessor.class);
        tmp.put(TypeScriptDescriptor.class, DefaultPropertyAccessor.class);
        tmp.put(TypePrimitiveDescriptor.class, DefaultPropertyAccessor.class);
        ACCESSOR_MAP = tmp;
    }

    private PropertyAccessorFactory() {
    }

    public static PropertyAccessorFactory get() {
        return new PropertyAccessorFactory();
    }

    public PropertyAccessorFactory typeDescriptor(TypeDescriptor typeDescriptor) {
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
                String.format("accessor for type %s not defined", typeDescriptor.type()));

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
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new ESBException(e);
        }
    }
}

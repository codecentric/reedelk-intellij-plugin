package com.esb.plugin.editor.properties.accessor;

import com.esb.api.exception.ESBException;
import com.esb.internal.commons.StringUtils;
import com.esb.plugin.component.domain.*;
import com.esb.plugin.component.type.unknown.UnknownPropertyType;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static com.esb.internal.commons.Preconditions.checkState;

public class PropertyAccessorFactory {

    private String propertyName;
    private TypeDescriptor typeDescriptor;
    private ComponentDataHolder dataHolder;

    private static final Map<Class<? extends TypeDescriptor>, Class<? extends PropertyAccessor>> ACCESSOR_MAP;

    static {
        Map<Class<? extends TypeDescriptor>, Class<? extends PropertyAccessor>> tmp = new HashMap<>();
        tmp.put(TypeEnumDescriptor.class, DefaultPropertyAccessor.class);
        tmp.put(TypeObjectDescriptor.class, ObjectPropertyAccessor.class);
        tmp.put(UnknownPropertyType.class, DefaultPropertyAccessor.class);
        tmp.put(TypePrimitiveDescriptor.class, DefaultPropertyAccessor.class);
        ACCESSOR_MAP = tmp;
    }

    private PropertyAccessorFactory() {
    }

    public static PropertyAccessorFactory get() {
        return new PropertyAccessorFactory();
    }

    public PropertyAccessorFactory propertyName(String propertyName) {
        this.propertyName = propertyName;
        return this;
    }

    public PropertyAccessorFactory typeDescriptor(TypeDescriptor typeDescriptor) {
        this.typeDescriptor = typeDescriptor;
        return this;
    }

    public PropertyAccessorFactory dataHolder(ComponentDataHolder dataHolder) {
        this.dataHolder = dataHolder;
        return this;
    }

    public PropertyAccessor build() {
        checkState(StringUtils.isNotBlank(propertyName), "property name must not be empty");
        checkState(typeDescriptor != null, "type descriptor must not be null");
        checkState(dataHolder != null, "data holder must not be null");

        Class<? extends PropertyAccessor> accessorClazz = ACCESSOR_MAP.get(typeDescriptor.getClass());
        return instantiate(accessorClazz);
    }

    private PropertyAccessor instantiate(Class<? extends PropertyAccessor> accessorClazz) {
        try {
            return accessorClazz
                    .getDeclaredConstructor(String.class, ComponentDataHolder.class)
                    .newInstance(propertyName, dataHolder);
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new ESBException(e);
        }
    }
}

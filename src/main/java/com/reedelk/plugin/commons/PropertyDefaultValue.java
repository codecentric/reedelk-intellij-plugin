package com.reedelk.plugin.commons;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.domain.TypeDescriptor;
import com.reedelk.plugin.converter.ValueConverterProvider;
import com.reedelk.runtime.api.annotation.Default;
import com.reedelk.runtime.converter.DefaultValues;

public class PropertyDefaultValue {

    private PropertyDefaultValue() {
    }

    /**
     * If the Default value was left blank, we use the default value. Otherwise
     * since default values can only be expressed as strings we must convert them to
     * the type of the current property: e.g a default value of  "3" for a long property
     * must be converted to 3L.
     *
     * @see Default#USE_DEFAULT_VALUE
     * @return the default value of the property type if one was specified, otherwise
     *  the default value for the give property type.
     */
    public static Object of(ComponentPropertyDescriptor descriptor) {
        String defaultValue = descriptor.getDefaultValue();
        TypeDescriptor propertyType = descriptor.getPropertyType();
        return Default.USE_DEFAULT_VALUE.equals(defaultValue) ?
                DefaultValues.defaultValue(propertyType.getType()) :
                ValueConverterProvider.forDefaults().forType(propertyType.getType()).from(defaultValue);
    }
}

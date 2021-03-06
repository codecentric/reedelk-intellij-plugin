package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.plugin.converter.ValueConverterProvider;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import de.codecentric.reedelk.module.descriptor.model.property.PropertyTypeDescriptor;
import de.codecentric.reedelk.runtime.api.annotation.InitValue;
import de.codecentric.reedelk.runtime.api.commons.DefaultValues;

public class InitPropertyValue {

    private InitPropertyValue() {
    }

    /**
     * If the Init value was left blank, we use the default type value. Otherwise
     * since init values can only be expressed as strings we must convert them to
     * the type of the current property: e.g a default value of  "3" for a long property
     * must be converted to 3L.
     *
     * @see com.reedelk.runtime.api.annotation.InitValue#USE_DEFAULT_VALUE
     * @return the default value of the property type if one was specified, otherwise
     *  the default value for the give property type.
     */
    public static Object of(PropertyDescriptor descriptor) {
        String initValue = descriptor.getInitValue();
        PropertyTypeDescriptor propertyType = descriptor.getType();
        return InitValue.USE_DEFAULT_VALUE.equals(initValue) ?
                DefaultValues.defaultValue(propertyType.getType()) :
                ValueConverterProvider.forDefaults().forType(propertyType.getType()).from(initValue);
    }
}

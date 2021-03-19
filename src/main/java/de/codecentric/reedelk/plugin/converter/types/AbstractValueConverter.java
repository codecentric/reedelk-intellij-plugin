package de.codecentric.reedelk.plugin.converter.types;

import de.codecentric.reedelk.plugin.converter.ValueConverter;
import de.codecentric.reedelk.runtime.converter.RuntimeConverters;

abstract class AbstractValueConverter<T> implements ValueConverter<T> {

    @Override
    public String toText(Object value) {
        return RuntimeConverters.getInstance().convert(value, String.class);
    }
}

package com.reedelk.plugin.converter.types;

import com.reedelk.plugin.converter.ValueConverter;
import com.reedelk.runtime.converter.RuntimeConverters;

abstract class AbstractValueConverter<T> implements ValueConverter<T> {

    @Override
    public String toText(Object value) {
        return RuntimeConverters.getInstance().convert(value, String.class);
    }
}

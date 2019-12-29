package com.reedelk.plugin.converter.types;

public class AsLongObject extends AbstractNumericValueConverter<Long> {

    @Override
    Class<Long> getClazz() {
        return Long.class;
    }
}

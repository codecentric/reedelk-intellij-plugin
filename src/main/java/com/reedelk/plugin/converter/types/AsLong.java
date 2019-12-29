package com.reedelk.plugin.converter.types;

public class AsLong extends AbstractNumericValueConverter<Long> {

    @Override
    Class<Long> getClazz() {
        return long.class;
    }
}

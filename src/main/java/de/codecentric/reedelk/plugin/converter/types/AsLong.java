package de.codecentric.reedelk.plugin.converter.types;

public class AsLong extends AbstractNumericValueConverter<Long> {

    @Override
    Class<Long> getClazz() {
        return long.class;
    }
}

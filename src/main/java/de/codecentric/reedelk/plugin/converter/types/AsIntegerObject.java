package de.codecentric.reedelk.plugin.converter.types;

public class AsIntegerObject extends AbstractNumericValueConverter<Integer> {

    @Override
    Class<Integer> getClazz() {
        return Integer.class;
    }
}

package de.codecentric.reedelk.plugin.converter.types;

public class AsInteger extends AbstractNumericValueConverter<Integer> {

    @Override
    Class<Integer> getClazz() {
        return int.class;
    }
}

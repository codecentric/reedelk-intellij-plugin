package de.codecentric.reedelk.plugin.converter.types;

public class AsDoubleObject extends AbstractNumericValueConverter<Double> {

    @Override
    Class<Double> getClazz() {
        return Double.class;
    }
}

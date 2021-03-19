package de.codecentric.reedelk.plugin.converter.types;

public class AsDouble extends AbstractNumericValueConverter<Double> {

    @Override
    Class<Double> getClazz() {
        return double.class;
    }
}

package de.codecentric.reedelk.plugin.validator;

import java.util.Collection;

public interface Validator {

    void validate(Collection<String> errors);

}

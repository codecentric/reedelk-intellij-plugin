package com.reedelk.plugin.component.scanner.property;

import com.reedelk.plugin.component.domain.ComponentPropertyDescriptor;
import com.reedelk.plugin.component.scanner.ComponentAnalyzerContext;
import io.github.classgraph.FieldInfo;

import java.util.Arrays;
import java.util.List;

public class HandlersChain {

    private static final List<Handler> HANDLERS = Arrays.asList(
            new NameHandler(),
            new TypeHandler(),
            new WhenHandler(),
            new HintHandler(),
            new VariableHandler(),
            new TransientHandler(),
            new DisplayNameHandler(),
            new DefaultValueHandler(),
            new AutocompleteContextHandler());

    public static ComponentPropertyDescriptor descriptor(FieldInfo propertyInfo, ComponentAnalyzerContext context) {
        ComponentPropertyDescriptor.Builder builder = ComponentPropertyDescriptor.builder();
        HANDLERS.forEach(handler -> handler.handle(propertyInfo, builder, context));
        return builder.build();
    }
}

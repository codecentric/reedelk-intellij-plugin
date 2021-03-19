package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.module.descriptor.model.property.PropertyDescriptor;
import de.codecentric.reedelk.runtime.api.commons.StringUtils;

import java.util.Optional;

public class TooltipContent {

    private final String example;
    private final String description;
    private final String defaultValue;

    public static TooltipContent from(PropertyDescriptor propertyDescriptor) {
        String example = propertyDescriptor.getExample();
        String description = propertyDescriptor.getDescription();
        String defaultValue = propertyDescriptor.getDefaultValue();
        return new TooltipContent(description, example, defaultValue);
    }

    private TooltipContent(String description, String example, String defaultValue) {
        this.example = example;
        this.description = description;
        this.defaultValue = defaultValue;
    }

    public Optional<String> build() {
        if (StringUtils.isBlank(this.description) &&
                StringUtils.isBlank(this.example) &&
                StringUtils.isBlank(this.defaultValue)) {
            return Optional.empty();
        }

        StringBuilder builder = new StringBuilder();
        if (StringUtils.isNotBlank(this.description)) {
            builder.append("<p>")
                    .append(this.description)
                    .append("<p>");
            if (StringUtils.isNotBlank(this.example) ||
                    StringUtils.isNotBlank(this.defaultValue)) {
                builder.append("<br>");
            }
        }
        if (StringUtils.isNotBlank(this.example)) {
            builder.append("<b>Example: </b>")
                    .append(this.example);
        }
        if (StringUtils.isNotBlank(this.defaultValue)) {
            if (StringUtils.isNotBlank(this.example) && builder.length() != 0) {
                builder.append("<br>");
            }
            builder.append("<b>Default: </b>")
                    .append(this.defaultValue);
        }
        return Optional.of(builder.toString());
    }
}

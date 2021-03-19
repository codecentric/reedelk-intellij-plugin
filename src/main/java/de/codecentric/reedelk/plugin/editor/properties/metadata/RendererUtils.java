package de.codecentric.reedelk.plugin.editor.properties.metadata;

import de.codecentric.reedelk.runtime.api.message.MessageAttributes;
import org.apache.commons.lang.StringEscapeUtils;

import static java.lang.String.format;

public class RendererUtils {

    private static final String TITLE_COLOR = "#666666";
    private static final String HTML_TITLE_WITH_TRAILING = "<html><b style=\"color: " + TITLE_COLOR + "\">%s</b> : <i>%s</i></html>";
    private static final String HTML_TITLE = "<html><b style=\"color: " + TITLE_COLOR + "\">%s</b></html>";
    private static final String HTML_TEXT = "<html>%s</html>";
    private static final String HTML_TYPE_TEXT = "<html><i>%s</i></html>";
    private static final String HTML_PROPERTY_ENTRY = "<html>%s : <i>%s</i></html>";

    public static String descriptionLabel() {
        return htmlTitle("description");
    }

    public static String payloadLabel(String displayType) {
        return htmlTitle("payload", displayType);
    }

    public static String attributeLabel() {
        return htmlTitle("attributes", MessageAttributes.class.getSimpleName());
    }

    public static String propertyEntry(String propertyName, String propertyType) {
        String escapedPropertyName = StringEscapeUtils.escapeHtml(propertyName);
        String escapedPropertyType = StringEscapeUtils.escapeHtml(propertyType);
        return format(HTML_PROPERTY_ENTRY, escapedPropertyName, escapedPropertyType);
    }

    public static String htmlTitle(String title, String trailingText) {
        String escapedTitle = StringEscapeUtils.escapeHtml(title);
        String escapedTrailingText = StringEscapeUtils.escapeHtml(trailingText);
        return format(HTML_TITLE_WITH_TRAILING, escapedTitle, escapedTrailingText);
    }

    public static String htmlTitle(String title) {
        String escapedTitle = StringEscapeUtils.escapeHtml(title);
        return format(HTML_TITLE, escapedTitle);
    }

    public static String htmlTypeText(String type) {
        String escapedText = StringEscapeUtils.escapeHtml(type);
        return format(HTML_TYPE_TEXT, escapedText);
    }

    public static String htmlText(String text) {
        String escapedText = StringEscapeUtils.escapeHtml(text);
        return format(HTML_TEXT, escapedText);
    }
}

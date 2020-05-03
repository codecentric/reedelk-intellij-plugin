package com.reedelk.plugin.editor.properties.commons;

import com.intellij.openapi.module.Module;
import com.reedelk.plugin.service.module.CompletionService;
import com.reedelk.plugin.service.module.impl.completion.ComponentIO;
import com.reedelk.plugin.service.module.impl.completion.Suggestion;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class PropertiesPanelIOContainer extends JEditorPane {

    private static final String CONTENT_TYPE = "text/html";
    private static final String HTML_TEMPLATE =
            "<div style=\"color: #333333; padding-left:5px;padding-right:10px;font-family:verdana;font-size:10px\">" +
                    "<p><b>Attributes:</b><br>" +
                    "<span style=\"font-size:5px\">%s</span>" +
                    "</p>" +
                    "</div>";

    public PropertiesPanelIOContainer(Module module, String componentFullyQualifiedName) {
        HTMLEditorKit kit = new HTMLEditorKit();
        setEditorKit(kit);

        StyleSheet styleSheet = kit.getStyleSheet();
        styleSheet.addRule(".attribute { font-size:8px; }");

        Optional<ComponentIO> componentIO = CompletionService.getInstance(module).componentIOOf(componentFullyQualifiedName);
        if (componentIO.isPresent()) {
            ComponentIO inputAndOutput = componentIO.get();
            List<Suggestion> outputAttributes = inputAndOutput.getOutputAttributes();
            StringBuilder builder = new StringBuilder();
            builder.append("<table class=\"attribute\">");
            outputAttributes.forEach(new Consumer<Suggestion>() {
                @Override
                public void accept(Suggestion suggestion) {
                    builder.append("<tr>");
                    builder.append("<td>").append(suggestion.lookupString()).append("</td>");
                    builder.append("<td>").append(suggestion.presentableType()).append("</td>");
                    builder.append("</tr>");
                }
            });
            builder.append("</table>");

            setContentType(CONTENT_TYPE);
            setText(String.format(HTML_TEMPLATE, builder.toString()));
        } else {
            setText("Not present");
        }
    }
}

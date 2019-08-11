package com.reedelk.plugin.editor.properties.widget.input.script.suggestion;

import com.intellij.openapi.editor.Document;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.List;
import java.util.Optional;

public interface SuggestionClient {

    Optional<Point> getPopupLocation(JTextComponent invoker);

    List<Suggestion> getSuggestions(JTextComponent invoker);

    void setSelectedText(JTextComponent invoker, Document document, String selectedValue);

}

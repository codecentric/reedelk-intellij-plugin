package com.esb.plugin.editor.properties.widget.input.script.suggestion;

import com.intellij.openapi.editor.Document;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.List;

public interface SuggestionClient {

    Point getPopupLocation(JTextComponent invoker);

    List<Suggestion> getSuggestions(JTextComponent invoker);

    void setSelectedText(JTextComponent invoker, Document document, String selectedValue);

}

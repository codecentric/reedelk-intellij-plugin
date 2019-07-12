package com.esb.plugin.editor.properties.widget.input.script;

import com.esb.plugin.component.domain.AutocompleteContext;
import com.esb.plugin.component.domain.AutocompleteVariable;
import com.esb.plugin.editor.properties.widget.input.script.trie.Trie;
import com.intellij.openapi.project.Project;

import java.util.Arrays;
import java.util.List;

public class JavascriptEditorFactory {

    private Project project;
    private String initialValue = "";
    private JavascriptEditorMode mode = JavascriptEditorMode.DEFAULT;

    private SuggestionProvider suggestionProvider;

    private List<AutocompleteContext> autocompleteContexts;
    private List<AutocompleteVariable> autocompleteVariables;

    public static JavascriptEditorFactory get() {
        return new JavascriptEditorFactory();
    }

    public JavascriptEditorFactory project(Project project) {
        this.project = project;
        return this;
    }

    public JavascriptEditorFactory mode(JavascriptEditorMode mode) {
        this.mode = mode;
        return this;
    }

    public JavascriptEditorFactory autocompleteVariables(List<AutocompleteVariable> autocompleteVariables) {
        this.autocompleteVariables = autocompleteVariables;
        return this;
    }


    public JavascriptEditorFactory autocompleteContexts(List<AutocompleteContext> autocompleteContexts) {
        this.autocompleteContexts = autocompleteContexts;
        return null;
    }

    public JavascriptEditor build() {
        Trie trie = new Trie();
        MessageSuggestions.SUGGESTIONS.forEach(trie::insert);
        JavascriptKeywords.KEYWORDS.forEach(trie::insert);

        List<JavascriptEditorContext.ContextVariable> DEFAULT_VARIABLES = Arrays.asList(
                new JavascriptEditorContext.ContextVariable("message", "Message"),
                new JavascriptEditorContext.ContextVariable("payload", "Object"),
                new JavascriptEditorContext.ContextVariable("inboundProperties", "Map"),
                new JavascriptEditorContext.ContextVariable("outboundProperties", "Map"));

        JavascriptEditorContext context = new JavascriptEditorContext(DEFAULT_VARIABLES);
        return new JavascriptEditor(project, mode, context, trie::searchByPrefix, initialValue);
    }

    public JavascriptEditorFactory initialValue(String initialValue) {
        this.initialValue = initialValue;
        return this;
    }

}

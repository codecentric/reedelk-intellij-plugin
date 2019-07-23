package com.esb.plugin.editor.properties.widget.input.script;

import com.esb.plugin.editor.properties.widget.input.script.suggestion.SuggestionTree;
import com.intellij.openapi.project.Project;

public class JavascriptEditorFactory {

    private Project project;
    private String initialValue = "";
    private JavascriptEditorMode mode;

    private ScriptContextManager context;

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

    public JavascriptEditorFactory context(ScriptContextManager context) {
        this.context = context;
        return this;
    }

    public JavascriptEditor build() {
        SuggestionTree suggestionTree = new SuggestionTree();
        MessageSuggestions.SUGGESTIONS.forEach(suggestionTree::insert);
        JavascriptKeywords.KEYWORDS.forEach(suggestionTree::insert);

        return new JavascriptEditor(project, mode, context, initialValue);
    }

    public JavascriptEditorFactory initialValue(String initialValue) {
        this.initialValue = initialValue;
        return this;
    }

}

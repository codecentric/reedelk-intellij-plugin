package com.esb.plugin.editor.properties.widget.input.script.editor;

import com.esb.plugin.editor.properties.widget.input.script.ScriptContextManager;
import com.esb.plugin.editor.properties.widget.input.script.suggestion.PredefinedJavascriptSuggestions;
import com.esb.plugin.editor.properties.widget.input.script.suggestion.PredefinedMessageSuggestions;
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
        PredefinedMessageSuggestions.SUGGESTIONS.forEach(suggestionTree::insert);
        PredefinedJavascriptSuggestions.KEYWORDS.forEach(suggestionTree::insert);
        if (JavascriptEditorMode.INLINE == mode) {
            return new JavascriptEditorInline(project, mode, context, initialValue);
        } else {
            return new JavascriptEditorDefault(project, mode, context, initialValue);
        }
    }

    public JavascriptEditorFactory initialValue(String initialValue) {
        this.initialValue = initialValue;
        return this;
    }
}

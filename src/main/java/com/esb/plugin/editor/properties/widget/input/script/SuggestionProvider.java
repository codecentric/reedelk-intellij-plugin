package com.esb.plugin.editor.properties.widget.input.script;

import java.util.List;

public interface SuggestionProvider {

    List<String> suggest(String text);
}

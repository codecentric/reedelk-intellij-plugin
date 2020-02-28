package com.reedelk.plugin.topic;

import com.intellij.util.messages.Topic;
import com.reedelk.plugin.editor.properties.CommitPropertiesListener;
import com.reedelk.plugin.editor.properties.selection.SelectionChangeListener;
import com.reedelk.plugin.service.module.impl.component.ComponentListUpdateNotifier;

import static com.reedelk.plugin.service.module.AutocompleteService.OnCompletionEvent;
import static com.reedelk.plugin.service.module.impl.configuration.ConfigurationServiceImpl.ConfigChangeListener;
import static com.reedelk.plugin.service.module.impl.script.ScriptServiceImpl.ScriptResourceChangeListener;

public class ReedelkTopics {

    public static final Topic<ConfigChangeListener> TOPIC_CONFIG_CHANGE =
            new Topic<>("config change events", ConfigChangeListener.class);

    public static final Topic<OnCompletionEvent> COMPLETION_EVENT_TOPIC =
            Topic.create("completion updated events", OnCompletionEvent.class);

    public static final Topic<ComponentListUpdateNotifier> COMPONENTS_UPDATE_EVENTS =
            Topic.create("component update events", ComponentListUpdateNotifier.class);

    public static final Topic<ScriptResourceChangeListener> TOPIC_SCRIPT_RESOURCE =
            new Topic<>("script resource change events", ScriptResourceChangeListener.class);

    public static final Topic<SelectionChangeListener> CURRENT_COMPONENT_SELECTION_EVENTS =
            Topic.create("current component selected events", SelectionChangeListener.class);

    public static final Topic<CommitPropertiesListener> COMMIT_COMPONENT_PROPERTIES_EVENTS =
            Topic.create("commit component properties events", CommitPropertiesListener.class);
}

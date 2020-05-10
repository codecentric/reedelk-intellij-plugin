package com.reedelk.plugin.commons;

import com.intellij.util.messages.Topic;
import com.reedelk.plugin.editor.properties.CommitPropertiesListener;
import com.reedelk.plugin.editor.properties.selection.SelectionChangeListener;
import com.reedelk.plugin.service.module.PlatformModuleService;
import com.reedelk.plugin.service.module.impl.component.componentio.OnComponentIO;

import static com.reedelk.plugin.service.module.impl.configuration.ConfigurationServiceImpl.ConfigChangeListener;
import static com.reedelk.plugin.service.module.impl.script.ScriptServiceImpl.ScriptResourceChangeListener;

public class Topics {

    public static final Topic<ConfigChangeListener> TOPIC_CONFIG_CHANGE =
            new Topic<>("config change events", ConfigChangeListener.class);

    public static final Topic<PlatformModuleService.OnCompletionEvent> COMPLETION_EVENT_TOPIC =
            Topic.create("completion updated events", PlatformModuleService.OnCompletionEvent.class);

    public static final Topic<PlatformModuleService.ModuleChangeNotifier> COMPONENTS_UPDATE_EVENTS =
            Topic.create("component update events", PlatformModuleService.ModuleChangeNotifier.class);

    public static final Topic<ScriptResourceChangeListener> TOPIC_SCRIPT_RESOURCE =
            new Topic<>("script resource change events", ScriptResourceChangeListener.class);

    public static final Topic<SelectionChangeListener> CURRENT_COMPONENT_SELECTION_EVENTS =
            Topic.create("current component selected events", SelectionChangeListener.class);

    public static final Topic<CommitPropertiesListener> COMMIT_COMPONENT_PROPERTIES_EVENTS =
            Topic.create("commit component properties events", CommitPropertiesListener.class);

    public static final Topic<OnComponentIO> ON_COMPONENT_IO =
            Topic.create("component input output ready", OnComponentIO.class);

    private Topics() {
    }
}

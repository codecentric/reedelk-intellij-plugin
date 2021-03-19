package de.codecentric.reedelk.plugin.commons;

import de.codecentric.reedelk.plugin.editor.properties.CommitPropertiesListener;
import de.codecentric.reedelk.plugin.editor.properties.selection.SelectionChangeListener;
import com.intellij.util.messages.Topic;

import static de.codecentric.reedelk.plugin.service.module.PlatformModuleService.OnComponentMetadataEvent;
import static de.codecentric.reedelk.plugin.service.module.PlatformModuleService.OnModuleEvent;
import static de.codecentric.reedelk.plugin.service.module.impl.configuration.ConfigurationServiceImpl.ConfigChangeListener;
import static de.codecentric.reedelk.plugin.service.module.impl.script.ScriptServiceImpl.ScriptResourceChangeListener;

public class Topics {

    public static final Topic<ConfigChangeListener> TOPIC_CONFIG_CHANGE =
            new Topic<>("config change events", ConfigChangeListener.class);

    public static final Topic<OnModuleEvent> COMPONENTS_UPDATE_EVENTS =
            Topic.create("component update events", OnModuleEvent.class);

    public static final Topic<ScriptResourceChangeListener> TOPIC_SCRIPT_RESOURCE =
            new Topic<>("script resource change events", ScriptResourceChangeListener.class);

    public static final Topic<SelectionChangeListener> CURRENT_COMPONENT_SELECTION_EVENTS =
            Topic.create("current component selected events", SelectionChangeListener.class);

    public static final Topic<CommitPropertiesListener> COMMIT_COMPONENT_PROPERTIES_EVENTS =
            Topic.create("commit component properties events", CommitPropertiesListener.class);

    public static final Topic<OnComponentMetadataEvent> ON_COMPONENT_IO =
            Topic.create("component input output ready", OnComponentMetadataEvent.class);

    private Topics() {
    }
}

package com.reedelk.plugin.editor.properties;

import com.intellij.util.messages.Topic;

public interface CommitPropertiesListener {

    Topic<CommitPropertiesListener> COMMIT_TOPIC = Topic.create("Properties Panel Commit", CommitPropertiesListener.class);

    void onCommit();
}

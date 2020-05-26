package com.reedelk.plugin.message;

import com.intellij.AbstractBundle;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class ReedelkBundle extends AbstractBundle {

    @NonNls public static final String BUNDLE = "messages.ReedelkBundle";
    private static final ReedelkBundle INSTANCE = new ReedelkBundle();

    protected ReedelkBundle() {
        super(BUNDLE);
    }

    @NotNull
    public static String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object ...params) {
        return INSTANCE.getMessage(key, params);
    }
}

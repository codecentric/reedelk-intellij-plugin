package com.reedelk.plugin.commons;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.util.Disposer;

public class DisposableUtils {

    private DisposableUtils() {
    }

    public static void dispose(Disposable disposable) {
        if (disposable != null) {
            Disposer.dispose(disposable);
        }
    }

    public static void dispose(Object object) {
        if (object instanceof Disposable) {
            dispose((Disposable) object);
        }
    }
}

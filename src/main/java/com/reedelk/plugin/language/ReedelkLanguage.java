package com.reedelk.plugin.language;

import com.intellij.lang.Language;

public class ReedelkLanguage extends Language {

    public static final ReedelkLanguage INSTANCE = new ReedelkLanguage();

    protected ReedelkLanguage() {
        super("reedelk");
    }
}

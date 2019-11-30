package com.reedelk.plugin.commons;

import com.intellij.psi.PsiDirectory;

public class IsResourceDirectory {

    private static final String RESOURCES_DIR_NAME = "resources";

    public static boolean of(PsiDirectory test) {
        return test.isDirectory() &&
                RESOURCES_DIR_NAME.equals(test.getName());
    }
}

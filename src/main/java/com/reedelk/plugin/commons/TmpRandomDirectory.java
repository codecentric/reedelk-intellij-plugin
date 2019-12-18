package com.reedelk.plugin.commons;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class TmpRandomDirectory {

    /**
     * @return the Path of a random directory created inside the tmp folder.
     */
    public static Path get() {
        String tmpDirectory = System.getProperty("java.io.tmpdir");
        String tmpFolder = UUID.randomUUID().toString();
        return Paths.get(tmpDirectory, tmpFolder);
    }
}
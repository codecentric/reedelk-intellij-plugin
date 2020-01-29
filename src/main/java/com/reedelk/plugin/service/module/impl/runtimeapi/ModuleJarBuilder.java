package com.reedelk.plugin.service.module.impl.runtimeapi;

import com.reedelk.plugin.exception.PluginException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Stream;

public class ModuleJarBuilder {

    private final String moduleName;
    private final String resourcesPath;
    private final String moduleVersion;
    private final String moduleJarFilePath;

    public ModuleJarBuilder(String moduleJarFilePath,
                            String resourcesPath,
                            String moduleVersion,
                            String moduleName) {
        this.resourcesPath = resourcesPath;
        this.moduleVersion = moduleVersion;
        this.moduleName = moduleName;
        this.moduleJarFilePath = moduleJarFilePath;
    }

    private File createModuleJarFile() throws IOException {
        File moduleJarFile = new File(moduleJarFilePath);
        moduleJarFile.getParentFile().mkdirs();
        moduleJarFile.createNewFile(); // if file already exists will do nothing
        return moduleJarFile;
    }

    public void run() throws IOException {
        Manifest manifest = new ModuleManifest(moduleVersion, moduleName);
        File moduleJarFile = createModuleJarFile();
        try (JarOutputStream target = new JarOutputStream(new FileOutputStream(moduleJarFile), manifest)) {
            Stream<Path> walk = Files.walk(Paths.get(resourcesPath));
            walk.forEach(path -> {
                try {
                    if (!path.toString().equals(resourcesPath)) {
                        add(resourcesPath, path.toFile(), target);
                    }
                } catch (IOException e) {
                    throw new PluginException("Could not read resources folder", e);
                }
            });
        }
    }

    private void add(String basePath, File source, JarOutputStream target) throws IOException {

        if (source.isDirectory()) {
            // Jar Entry is a directory
            String name = source.getPath().substring(basePath.length());
            name = name.replace("\\", "/");
            if (!name.isEmpty()) {
                if (!name.endsWith("/")) name += "/";
                if (name.startsWith("/")) name = name.substring(1);
                JarEntry entry = new JarEntry(name);
                entry.setTime(source.lastModified());
                target.putNextEntry(entry);
                target.closeEntry();
            }

        } else {
            // Jar Entry is a file
            try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(source))) {
                String name = source.getPath().substring(basePath.length());
                name = name.replace("\\", "/");
                if (name.startsWith("/")) name = name.substring(1);
                JarEntry entry = new JarEntry(name);
                entry.setTime(source.lastModified());
                target.putNextEntry(entry);

                byte[] buffer = new byte[1024];
                while (true) {
                    int count = in.read(buffer);
                    if (count == -1)
                        break;
                    target.write(buffer, 0, count);
                }
                target.closeEntry();
            }
        }
    }
}

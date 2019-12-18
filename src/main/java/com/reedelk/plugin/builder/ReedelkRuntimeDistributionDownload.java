package com.reedelk.plugin.builder;

import com.intellij.util.io.ZipUtil;
import com.reedelk.plugin.service.module.impl.http.RestClientProvider;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import static com.reedelk.plugin.commons.Defaults.NameConvention;

class ReedelkRuntimeDistributionDownload {

    private static final String DOWNLOAD_LATEST_DISTRIBUTION =
            NameConvention.RUNTIME_ONLINE_DISTRIBUTION_URL +
            NameConvention.RUNTIME_ONLINE_DISTRIBUTION_ZIP_FILE_NAME;

    private ReedelkRuntimeDistributionDownload() {
    }

    static Path downloadAndUnzip() throws IOException {
        Request request = new Request.Builder().url(DOWNLOAD_LATEST_DISTRIBUTION).get().build();
        try (Response response = RestClientProvider.getInstance().newCall(request).execute()) {

            if (response.body() == null) {
                throw new IOException("Body was null");
            }

            try (InputStream initialStream = response.body().byteStream()) {

                Path tmpFilePath = getTmpFile();
                FileUtils.copyInputStreamToFile(initialStream, tmpFilePath.toFile());

                File unzipDestinationFolder = tmpFilePath.getParent().toFile();
                ZipUtil.extract(tmpFilePath.toFile(), unzipDestinationFolder, (dir, name) -> true);

                String runtimeRootFolderName = findReedelkRuntimeRootFolder(unzipDestinationFolder)
                        .orElseThrow(() -> new IOException("Could not find root folder in zip file"));

                return Paths.get(unzipDestinationFolder.getPath(), runtimeRootFolderName);
            }
        }
    }

    private static Optional<String> findReedelkRuntimeRootFolder(File destination) {
        String[] rootFolder = destination.list((dir, name) -> dir.isDirectory() &&
                name.startsWith(NameConvention.RUNTIME_DISTRIBUTION_ROOT_FOLDER_PREFIX));
        if (rootFolder == null || rootFolder.length == 0) return Optional.empty();
        else return Optional.of(rootFolder[0]);
    }

    private static Path getTmpFile() {
        String tmpDirectory = System.getProperty("java.io.tmpdir");
        String tmpFolder = UUID.randomUUID().toString();
        return Paths.get(tmpDirectory, tmpFolder, NameConvention.RUNTIME_ONLINE_DISTRIBUTION_ZIP_FILE_NAME);
    }
}

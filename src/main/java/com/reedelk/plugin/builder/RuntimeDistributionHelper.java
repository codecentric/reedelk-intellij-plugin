package com.reedelk.plugin.builder;

import com.intellij.util.io.ZipUtil;
import com.reedelk.plugin.commons.TmpRandomDirectory;
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

import static com.reedelk.plugin.commons.Defaults.NameConvention;
import static com.reedelk.plugin.message.ReedelkBundle.message;

class RuntimeDistributionHelper {

    private static final String DOWNLOAD_LATEST_DISTRIBUTION_URL =
            NameConvention.RUNTIME_ONLINE_DISTRIBUTION_URL +
                    NameConvention.RUNTIME_ONLINE_DISTRIBUTION_ZIP_FILE_NAME;

    private RuntimeDistributionHelper() {
    }

    static Path downloadAndUnzip() throws IOException {
        Request request = new Request.Builder().url(DOWNLOAD_LATEST_DISTRIBUTION_URL).get().build();
        try (Response response = RestClientProvider.getInstance().newCall(request).execute()) {

            if (response.body() == null) {
                String message = message("runtimeBuilder.downloading.distribution.error.root.response.body.null");
                throw new IOException(message);
            }

            try (InputStream initialStream = response.body().byteStream()) {

                // Prepare directories
                Path tmpRandomDirectory = TmpRandomDirectory.get();
                Path distributionZipFilePath =
                        Paths.get(tmpRandomDirectory.toString(), NameConvention.RUNTIME_ONLINE_DISTRIBUTION_ZIP_FILE_NAME);
                File distributionZipFile = distributionZipFilePath.toFile();
                FileUtils.copyInputStreamToFile(initialStream, distributionZipFile);

                // Unzip the distribution
                ZipUtil.extract(distributionZipFile, tmpRandomDirectory.toFile(), (dir, name) -> true);

                // Find distribution root folder
                String runtimeRootFolderName = findDirectoryWithPrefixInDestination(
                        tmpRandomDirectory.toFile(),
                        NameConvention.RUNTIME_DISTRIBUTION_ROOT_FOLDER_PREFIX)
                        .orElseThrow(() -> new IOException(
                                message("runtimeBuilder.downloading.distribution.error.root.folder",
                                        NameConvention.RUNTIME_DISTRIBUTION_ROOT_FOLDER_PREFIX,
                                        DOWNLOAD_LATEST_DISTRIBUTION_URL)));

                // The final path is the tmp random directory + the distribution folder name
                return Paths.get(tmpRandomDirectory.toString(), runtimeRootFolderName);
            }
        }
    }

    private static Optional<String> findDirectoryWithPrefixInDestination(File destination, String directoryPrefix) {
        String[] rootFolder = destination.list((dir, name) -> dir.isDirectory() && name.startsWith(directoryPrefix));
        return (rootFolder == null || rootFolder.length == 0) ?
                Optional.empty() :
                Optional.of(rootFolder[0]);
    }
}

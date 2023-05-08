package ge.edu.sangu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        log.debug("Application started");
        if (args.length < 2) {
            log.error("[Usage]: app [url] [download-folder]");
            return;
        }

        URL url = getUrl(args[0]);
        Path downloadFolder = getDownloadFolder(args[1]);

        try {
            ImageDownloader imageDownloader = new ImageDownloader(url, downloadFolder);
            imageDownloader.run();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.debug("Application finished");
    }

    private static Path getDownloadFolder(String downloadFolderPathString) {
        Path downloadFolderPath = Paths.get(downloadFolderPathString);
        if (Files.notExists(downloadFolderPath)) {
            try {
                Files.createDirectories(downloadFolderPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (!Files.isDirectory(downloadFolderPath)) {
            throw new IllegalArgumentException(String.format("Not a directory: %s\n", downloadFolderPathString));
        }
        return downloadFolderPath;
    }

    private static URL getUrl(String urlString) {
        try {
            return URI.create(urlString).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(String.format("Not a valid URL passed: %s\n", urlString));
        }
    }
}
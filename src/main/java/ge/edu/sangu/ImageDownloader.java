package ge.edu.sangu;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Image downloader class that requires URL and download folders. It uses JSoup library to parse root page,
 * find images and download them.
 *
 * @author Vakho10
 */
public class ImageDownloader implements Runnable {

    private static final Logger log = LogManager.getLogger(ImageDownloader.class);

    private final URL url;
    private final Path downloadFolder;

    /**
     * @param url            Root website url from where the images will be downloaded
     * @param downloadFolder Folder where downloaded images will be placed
     */
    public ImageDownloader(URL url, Path downloadFolder) {
        this.url = url;
        this.downloadFolder = downloadFolder;
    }

    @Override
    public void run() {
        Document doc = null;
        try {
            doc = Jsoup.connect(url.toString()).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Elements imageElements = doc.select("img[src]");
        for (Element imageElement : imageElements) {
            String imageDownloadUrl = imageElement.absUrl("src");
            log.info(String.format("Downloading image: %s", imageDownloadUrl));
            try {
                downloadImage(getImageFilename(imageDownloadUrl), imageDownloadUrl);
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }

    /**
     * Reads image name and extension from a URL string.
     *
     * @param imageDownloadUrl URL string
     * @return Image filename with extension
     */
    public String getImageFilename(String imageDownloadUrl) {
        return imageDownloadUrl.substring(imageDownloadUrl.lastIndexOf("/") + 1);
    }

    /**
     * Downloads image to root output folder.
     *
     * @param filename
     * @param url
     * @throws IOException
     */
    public void downloadImage(String filename, String url) throws IOException {
        Response response = Jsoup.connect(url).ignoreContentType(true).execute();
        Path downloadImageFile = downloadFolder.resolve(filename);
        Files.write(downloadImageFile, response.bodyAsBytes());
    }
}

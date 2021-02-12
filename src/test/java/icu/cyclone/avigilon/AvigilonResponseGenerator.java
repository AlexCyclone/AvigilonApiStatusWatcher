package icu.cyclone.avigilon;

import com.google.common.base.Strings;
import icu.cyclone.avigilon.services.PropertyService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.stream.Collectors;

/**
 * @author Aleksey Babanin
 * @since 2021/02/12
 */
public class AvigilonResponseGenerator {
    private static final PropertyService props = PropertyService.getInstance();
    private static final String REST_URI = props.getProperty("rest.uri");
    private static final String EMPTY = "";
    private static final String EMPTY_JSON = "{}";
    private static final String RIGHT_SLASH ="/";
    private static final String EXTENSION_JSON =".json";

    private final String resourcePath;
    private final String urlPrefix;

    public AvigilonResponseGenerator(String serviceUrl, String resourcePath) {
        this.resourcePath = resourcePath;
        urlPrefix = serviceUrl + REST_URI;
    }

    public String getResponse(String url) {
        return getJson(getJsonFilename(url));
    }

    private String getJsonFilename(String url) {
        if (Strings.isNullOrEmpty(url)) {
            return EMPTY;
        }
        return url.replaceFirst(urlPrefix, EMPTY).replaceAll(RIGHT_SLASH, EMPTY);
    }

    private String getJson(String fileName) {
        fileName += EXTENSION_JSON;
        fileName = resourcePath + RIGHT_SLASH + fileName;
        ClassLoader classLoader = getClass().getClassLoader();
        URL fileURL = classLoader.getResource(fileName);

        if (fileURL == null) {
            return EMPTY_JSON;
        }

        File file = new File(fileURL.getFile());
        if (!file.exists() || !file.isFile()) {
            return EMPTY_JSON;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException suppress) {
            return EMPTY_JSON;
        }
    }
}

package icu.cyclone.avigilon.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Aleksey Babanin
 * @since 2021/02/10
 */
public class PropertyService {
    private static final String PATH = "application.properties";

    public static PropertyService getInstance() {
        return PropertyServiceHolder.INSTANCE;
    }

    private static class PropertyServiceHolder {
        private static final PropertyService INSTANCE = new PropertyService();
    }

    final Properties prop = new Properties();

    private PropertyService() {
        init();
    }

    private void init() {
        try (InputStream input = PropertyService.class.getClassLoader().getResourceAsStream(PATH)) {
            if (input != null) {
                prop.load(input);
            }
        } catch (IOException ignored) {
        }
    }

    public String getProperty(String key) {
        return prop.getProperty(key, "unavailable");
    }
}

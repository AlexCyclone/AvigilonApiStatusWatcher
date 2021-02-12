package icu.cyclone.avigilon.utils;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

/**
 * @author Aleksey Babanin
 * @since 2021/02/02
 */
public class JsonUtils {
    public static Map<String, Object> parseToMap(String jsonString) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        return Strings.isNullOrEmpty(jsonString) ? Collections.emptyMap() : gson.fromJson(jsonString, type);
    }

    public static String getJsonString(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(Map<String, Object> jsonMap, String... name) {
        Object value = null;
        for (int i = 0; i < name.length; i++) {
            value = getValue(jsonMap, name[i]);

            if (i < name.length - 1) {
                validateMap(value, name[i + 1]);
                jsonMap = (Map<String, Object>) value;
            }
        }
        return (T) value;
    }

    private static void validateMap(Object value, String name) {
        if (!(value instanceof Map)) {
            throw new IllegalArgumentException("Parameter \"" + name + "\" not found");
        }
    }

    private static Object getValue(Map<String, Object> jsonMap, String name) {
        Object value = jsonMap.get(name);
        if (value == null) {
            throw new IllegalArgumentException("Parameter \"" + name + "\" not found");
        }
        return value;
    }
}

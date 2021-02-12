package icu.cyclone.avigilon.utils;

import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author Aleksey Babanin
 * @since 2021/02/01
 */
public class AvigilonTokenGenerator {
    private static final String SEPARATOR = ":";
    private static final long TIME_SHIFT = 60 * 5; // 5 minutes

    public static String generate(String userNonce, String userKey, String identifier) {

        StringBuilder stringBuilder = new StringBuilder();
        long timestamp = new Date().getTime() / 1000 - TIME_SHIFT;
        stringBuilder
                .append(userNonce)
                .append(SEPARATOR)
                .append(timestamp)
                .append(SEPARATOR)
                .append(encode(timestamp, userKey));
        if (!Strings.isNullOrEmpty(identifier)) {
            stringBuilder.append(SEPARATOR).append(identifier);
        }
        return stringBuilder.toString();
    }

    private static String encode(long timestamp, String userKey) {
        //noinspection UnstableApiUsage
        return Hashing.sha256().hashString(timestamp + userKey, StandardCharsets.UTF_8).toString();
    }
}

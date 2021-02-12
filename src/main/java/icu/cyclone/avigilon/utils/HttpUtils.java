package icu.cyclone.avigilon.utils;

import com.google.common.base.Strings;
import com.google.common.net.HttpHeaders;
import icu.cyclone.avigilon.exception.CommunicationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Aleksey Babanin
 * @since 2021/02/02
 */
public class HttpUtils {
    private static final int TIMEOUT = 3000;
    private static final String EMPTY = "";
    private static final String EQUALLY = "=";
    private static final String QUESTION_MARK = "?";
    private static final String AMPERSAND = "&";

    public static String sendRequest(String urlString, Map<String, String> queryParams, String requestMethod, Map<String, ?> requestHeaders, String content) throws CommunicationException {
        HttpURLConnection connection = null;
        int status;
        try {
            URL url = new URL(urlString + getQueryParams(queryParams));
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(requestMethod);
            setDefaultParameters(connection);
            addHeaders(connection, requestHeaders);
            addContent(connection, content);
            connection.connect();

            status = connection.getResponseCode();

            if (status >= 200 && status <= 299) {
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    return br.lines().collect(Collectors.joining(System.lineSeparator()));
                }
            }
        } catch (IOException | NoSuchAlgorithmException | KeyManagementException e) {
            throw new CommunicationException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception ignored) {
                }
            }
        }
        throw new CommunicationException("Incorrect connection status " + status);
    }

    private static String getQueryParams(Map<String, String> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return EMPTY;
        }
        return queryParams.entrySet()
                .stream()
                .map(entry -> entry.getKey() + EQUALLY + entry.getValue())
                .collect(Collectors.joining(AMPERSAND, QUESTION_MARK, EMPTY));
    }

    private static void setDefaultParameters(HttpURLConnection connection) throws NoSuchAlgorithmException, KeyManagementException {
        connection.setConnectTimeout(TIMEOUT);
        connection.setReadTimeout(TIMEOUT);
        HttpsTrustModifier.discardChecking(connection);
    }

    private static void addContent(HttpURLConnection connection, String content) throws IOException {
        if (Strings.isNullOrEmpty(content)) {
            connection.setRequestProperty(HttpHeaders.CONTENT_LENGTH, String.valueOf(0));
        } else {
            connection.setRequestProperty(HttpHeaders.CONTENT_LENGTH, String.valueOf(content.length()));
            connection.setDoOutput(true);
            try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
                writer.write(content);
            }
        }
    }

    private static void addHeaders(HttpURLConnection connection, Map<String, ?> requestHeaders) {
        if (requestHeaders != null) {
            for (Map.Entry<String, ?> entry : requestHeaders.entrySet()) {
                connection.addRequestProperty(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
    }
}

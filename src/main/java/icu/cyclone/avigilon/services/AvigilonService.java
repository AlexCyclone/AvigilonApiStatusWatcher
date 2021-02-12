package icu.cyclone.avigilon.services;

import com.google.common.net.HttpHeaders;
import icu.cyclone.avigilon.entities.Camera;
import icu.cyclone.avigilon.entities.Server;
import icu.cyclone.avigilon.entities.Site;
import icu.cyclone.avigilon.entities.converters.CameraConverter;
import icu.cyclone.avigilon.entities.converters.Converter;
import icu.cyclone.avigilon.entities.converters.ServerConverter;
import icu.cyclone.avigilon.entities.converters.SiteConverter;
import icu.cyclone.avigilon.exception.CommunicationException;
import icu.cyclone.avigilon.utils.AvigilonTokenGenerator;
import icu.cyclone.avigilon.utils.ConvertUtil;
import icu.cyclone.avigilon.utils.HttpUtils;
import icu.cyclone.avigilon.utils.IpUtils;
import icu.cyclone.avigilon.utils.JsonUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Aleksey Babanin
 * @since 2021/02/02
 */
public class AvigilonService {
    private static final PropertyService props = PropertyService.getInstance();
    private static final String REST_URI = props.getProperty("rest.uri");
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST = "POST";
    private static final String CLIENT_NAME = "REST_PLUGIN";
    private static final String MEDIA_TYPE_JSON = "application/json";
    private static final String STATUS_OK = "success";
    private static final String VERBOSITY_HIGH = "HIGH";
    private static final String QUERY_PARAM_VERBOSITY = "verbosity";
    private static final String HEADER_X_AVG_SESSION = "x-avg-session";
    private static final String UNDEFINED = "undefined";

    private static final String REQUEST_LOGIN = "login";
    private static final String REQUEST_LOGOUT = "logout";
    private static final String REQUEST_CAMERAS = "cameras";
    private static final String REQUEST_ENTITIES = "entities";
    private static final String REQUEST_SERVERS = "server/ids";
    private static final String REQUEST_SITES = "sites";

    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_PASSWORD = "password";
    private static final String PARAM_CLIENT_NAME = "clientName";
    private static final String PARAM_TOKEN = "authorizationToken";

    private static final String KEY_STATUS = "status";
    private static final String KEY_RESULT = "result";
    private static final String KEY_SESSION = "session";
    private static final String KEY_CAMERAS = "cameras";
    private static final String KEY_ENTITIES = "entities";
    private static final String KEY_SERVERS = "servers";
    private static final String KEY_SITES = "sites";

    private final String urlString;
    private final String networkPrefix;
    private final String username;
    private final String password;
    private final String token;
    private String session;
    private String serverHost;

    public AvigilonService(String urlString, String networkPrefix, String username, String password, String userNonce, String userKey) {
        this.urlString = urlString;
        this.networkPrefix = networkPrefix;
        this.username = username;
        this.password = password;
        token = AvigilonTokenGenerator.generate(userNonce, userKey, null);
    }

    public void login() {
        String responseString = HttpUtils.sendRequest(getRestUri(REQUEST_LOGIN), null, METHOD_POST, getLoginHeaders(), getLoginBody());
        Map<String, Object> map = JsonUtils.parseToMap(responseString);
        verifyResponse(map);
        session = JsonUtils.getValue(map, KEY_RESULT, KEY_SESSION);
    }

    public void logout() {
        if (session != null) {
            HttpUtils.sendRequest(getRestUri(REQUEST_LOGOUT), null, METHOD_POST, getRequestHeaders(), getLogoutBody());
            session = null;
        }
    }

    public List<Camera> getCameras() {
        Converter<Camera> converter = new CameraConverter(getServers());
        return ConvertUtil.convert(getListCameraObject(), converter);
    }

    public List<Server> getServers() {
        Converter<Server> converter = new ServerConverter(getServerHost(), getPreferredSite());
        return ConvertUtil.convert(getListServerObject(), converter);
    }

    public List<Site> getSites() {
        Converter<Site> converter = new SiteConverter();
        return ConvertUtil.convert(getListSiteObject(), converter);
    }

    public Site getPreferredSite() {
        List<Site> sites = getSites();
        if (!sites.isEmpty()) {
            return sites.get(0);
        }
        return new Site();
    }

    public String getSession() {
        return session;
    }

    public String getServerHost() {
        if (serverHost == null) {
            defineServerHost();
        }
        return serverHost;
    }

    protected List<Object> getListCameraObject() {
        verifySession();
        String responseString = HttpUtils.sendRequest(getRestUri(REQUEST_CAMERAS), getVerbosityParam(), METHOD_GET, getRequestHeaders(), null);
        Map<String, Object> map = JsonUtils.parseToMap(responseString);
        verifyResponse(map);
        return JsonUtils.getValue(map, KEY_RESULT, KEY_CAMERAS);
    }

    protected List<Object> getListEntityObject() {
        verifySession();
        String responseString = HttpUtils.sendRequest(getRestUri(REQUEST_ENTITIES), null, METHOD_GET, getRequestHeaders(), null);
        Map<String, Object> map = JsonUtils.parseToMap(responseString);
        verifyResponse(map);
        return JsonUtils.getValue(map, KEY_RESULT, KEY_ENTITIES);
    }

    protected List<Object> getListServerObject() {
        verifySession();
        String responseString = HttpUtils.sendRequest(getRestUri(REQUEST_SERVERS), null, METHOD_GET, getRequestHeaders(), null);
        Map<String, Object> map = JsonUtils.parseToMap(responseString);
        verifyResponse(map);
        return JsonUtils.getValue(map, KEY_RESULT, KEY_SERVERS);
    }

    protected List<Object> getListSiteObject() {
        verifySession();
        String responseString = HttpUtils.sendRequest(getRestUri(REQUEST_SITES), getSessionParam(), METHOD_GET, getRequestHeaders(), null);
        Map<String, Object> map = JsonUtils.parseToMap(responseString);
        verifyResponse(map);
        return JsonUtils.getValue(map, KEY_RESULT, KEY_SITES);
    }

    private void defineServerHost() {
        String host = IpUtils.getHost(urlString);
        if (host == null || IpUtils.isLoopback(host)) {
            List<String> addresses = IpUtils.getIpList();
            host = findExpectedAddress(addresses);
            if (host == null) {
                host = findFirstAddress(addresses);
            }
        }
        serverHost = host;
    }

    private String findExpectedAddress(List<String> addresses) {
        return addresses.stream()
                .filter(Objects::nonNull)
                .filter(a -> a.startsWith(networkPrefix))
                .findFirst().orElse(null);
    }

    private String findFirstAddress(List<String> addresses) {
        return addresses.stream()
                .filter(Objects::nonNull)
                .sorted()
                .findFirst().orElse(UNDEFINED);
    }

    private String getLoginBody() {
        Map<String, String> loginBody = new HashMap<>(4, 1);
        loginBody.put(PARAM_USERNAME, username);
        loginBody.put(PARAM_PASSWORD, password);
        loginBody.put(PARAM_CLIENT_NAME, CLIENT_NAME);
        loginBody.put(PARAM_TOKEN, token);
        return JsonUtils.getJsonString(loginBody);
    }

    private String getLogoutBody() {
        return JsonUtils.getJsonString(getSessionParam());
    }

    private Map<String, String> getLoginHeaders() {
        Map<String, String> loginHeaders = new HashMap<>(2, 1);
        loginHeaders.put(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE_JSON);
        loginHeaders.put(HttpHeaders.ACCEPT, MEDIA_TYPE_JSON);
        return loginHeaders;
    }

    private Map<String, String> getRequestHeaders() {
        Map<String, String> requestHeaders = new HashMap<>(3, 1);
        requestHeaders.put(HttpHeaders.CONTENT_TYPE, MEDIA_TYPE_JSON);
        requestHeaders.put(HttpHeaders.ACCEPT, MEDIA_TYPE_JSON);
        requestHeaders.put(HEADER_X_AVG_SESSION, session);
        return requestHeaders;
    }

    private Map<String, String> getVerbosityParam() {
        Map<String, String> queryParam = new HashMap<>(1, 1);
        queryParam.put(QUERY_PARAM_VERBOSITY, VERBOSITY_HIGH);
        return queryParam;
    }

    private Map<String, String> getSessionParam() {
        Map<String, String> queryParam = new HashMap<>(1, 1);
        queryParam.put(KEY_SESSION, session);
        return queryParam;
    }

    private String getRestUri(String request) {
        return urlString + REST_URI + request;
    }

    private void verifyResponse(Map<String, Object> map) {
        String status = JsonUtils.getValue(map, KEY_STATUS);
        if (!STATUS_OK.equals(status)) {
            throw new CommunicationException("Incorrect response status: " + status);
        }
    }

    private void verifySession() {
        if (session == null) {
            throw new CommunicationException("Incorrect session state");
        }
    }
}
package icu.cyclone.avigilon.utils;

import icu.cyclone.avigilon.services.PropertyService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Aleksey Babanin
 * @since 2021/02/02
 */
public class ArgumentParser {
    private static final PropertyService props = PropertyService.getInstance();
    private static final String DEFAULT_URL = props.getProperty("default.url");
    private static final String DEFAULT_USERNAME = props.getProperty("default.username");
    private static final String DEFAULT_PASSWORD = props.getProperty("default.password");
    private static final String DEFAULT_NONCE = props.getProperty("default.nonce");
    private static final String DEFAULT_KEY = props.getProperty("default.key");
    private static final String DEFAULT_NETWORK_PREFIX = props.getProperty("default.network.prefix");
    private static final String DEFAULT_UNAVAILABLE = props.getProperty("default.unavailable");

    private enum Args {
        URL("--url", DEFAULT_URL, "URL string to Avigilon Web Endpoint"),
        USERNAME("--username", DEFAULT_USERNAME, "Username to Avigilon Service"),
        PASSWORD("--password", DEFAULT_PASSWORD, "Password to Avigilon Service"),
        NONCE("--nonce", DEFAULT_NONCE, "User Nonce string"),
        KEY("--key", DEFAULT_KEY, "User Key string"),
        NETWORK_PREFIX("--net", DEFAULT_NETWORK_PREFIX, "Prefix identified ip for external server interface"),
        DEBUG("--debug", DEFAULT_UNAVAILABLE, "Debug mode"),
        HELP("--help", DEFAULT_UNAVAILABLE, "Show this message");

        private final String arg;
        private final String defaultValue;
        private final String helpMessage;

        Args(String arg, String defaultValue, String helpMessage) {
            this.arg = arg;
            this.defaultValue = defaultValue;
            this.helpMessage = helpMessage;
        }
    }

    private static final String HELP_START_MESSAGE = "Approved parameters list:" + System.lineSeparator();
    private static final String HELP_FORMAT = "    %s - %s (default value [%s])%n";

    private static final Set<String> booleanArgs = getBooleanArgs();

    private static Set<String> getBooleanArgs() {
        return Arrays.stream(Args.values())
                .filter(arg -> DEFAULT_UNAVAILABLE.equals(arg.defaultValue))
                .map(arg -> arg.arg)
                .collect(Collectors.toSet());
    }

    private final String[] args;
    private Map<String, String> paramsMap;

    public ArgumentParser(String[] args) {
        this.args = args;
        initDefaults();
        parse();
    }

    private void initDefaults() {
        paramsMap = new HashMap<>(Args.values().length - 1, 1);
        for (Args arg : Args.values()) {
            String defaultValue = arg.defaultValue;
            paramsMap.put(arg.arg, defaultValue);
        }
    }

    private void parse() {
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (booleanArgs.contains(args[i])) {
                    readBooleanArg(args[i]);
                } else {
                    readArg(args, i);
                }
            }
        }
    }

    private void readBooleanArg(String arg) {
        if (paramsMap.containsKey(arg)) {
            paramsMap.put(arg, String.valueOf(true));
        }
    }

    private void readArg(String[] args, int i) {
        if (i < args.length - 1 && paramsMap.containsKey(args[i])) {
            paramsMap.put(args[i], args[i + 1]);
        }
    }

    public static String getHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append(HELP_START_MESSAGE);
        for (Args arg : Args.values()) {
            sb.append(String.format(HELP_FORMAT, arg.arg, arg.helpMessage, arg.defaultValue));
        }
        return sb.toString();
    }

    public String getUrlString() {
        return paramsMap.get(Args.URL.arg);
    }

    public String getUsername() {
        return paramsMap.get(Args.USERNAME.arg);
    }

    public String getPassword() {
        return paramsMap.get(Args.PASSWORD.arg);
    }

    public String getUserNonce() {
        return paramsMap.get(Args.NONCE.arg);
    }

    public String getUserKey() {
        return paramsMap.get(Args.KEY.arg);
    }

    public String getNetworkPrefix() {
        return paramsMap.get(Args.NETWORK_PREFIX.arg);
    }

    public boolean isDebug() {
        return Boolean.parseBoolean(paramsMap.get(Args.DEBUG.arg));
    }

    public boolean isHelp() {
        return Boolean.parseBoolean(paramsMap.get(Args.HELP.arg));
    }

}

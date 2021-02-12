package icu.cyclone.avigilon.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Aleksey Babanin
 * @since 2021/02/10
 */
public class IpUtils {
    private static final String LOCALHOST = "localhost";
    private static final String LOOPBACK_PREFIX = "127.";

    public static boolean isLoopback(String address) {
        return address != null &&
                (LOCALHOST.equalsIgnoreCase(address) ||
                        address.startsWith(LOOPBACK_PREFIX));
    }

    public static String getHost(String urlString) {
        try {
            URL url = new URL(urlString);
            return url.getHost();
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public static List<String> getIpList() {
        List<NetworkInterface> interfaces;
        try {
            interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        } catch (SocketException e) {
            return Collections.emptyList();
        }
        return interfaces.stream()
                .flatMap(NetworkInterface::inetAddresses)
                .filter(inetAddress -> inetAddress instanceof Inet4Address)
                .map(InetAddress::getHostAddress)
                .filter(Objects::nonNull)
                .filter(s -> !s.startsWith(LOOPBACK_PREFIX))
                .collect(Collectors.toList());
    }
}

package icu.cyclone.avigilon.entities.converters;

import icu.cyclone.avigilon.entities.Server;
import icu.cyclone.avigilon.entities.Site;
import java.util.Map;

/**
 * @author Aleksey Babanin
 * @since 2021/02/10
 */
public class ServerConverter implements Converter<Server> {
    private final String ipAddress;
    private final Site site;

    public ServerConverter(String ipAddress, Site site) {
        this.ipAddress = ipAddress;
        this.site = site;
    }

    @Override
    public Server convert(Object o) {
        if (o instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) o;
            Server server = new Server();
            server.setId(String.valueOf(map.get("id")));
            server.setName(String.valueOf(map.get("name")));
            server.setIpAddress(ipAddress);
            server.setSite(site);
            return server;
        }
        return null;
    }
}

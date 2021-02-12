package icu.cyclone.avigilon.entities.converters;

import icu.cyclone.avigilon.entities.Camera;
import icu.cyclone.avigilon.entities.Server;
import java.util.List;
import java.util.Map;

/**
 * @author Aleksey Babanin
 * @since 2021/02/10
 */
public class CameraConverter implements Converter<Camera> {
    final List<Server> servers;

    public CameraConverter(List<Server> servers) {
        this.servers = servers;
    }

    @Override
    public Camera convert(Object o) {
        if (o instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) o;
            Camera camera = new Camera();
            camera.setId(String.valueOf(map.get("id")));
            camera.setName(String.valueOf(map.get("name")));
            camera.setConnectionState(String.valueOf(map.get("connectionState")));
            camera.setConnected(Boolean.parseBoolean(String.valueOf(map.get("connected"))));
            camera.setAvailable(Boolean.parseBoolean(String.valueOf(map.get("available"))));
            camera.setPhysicalAddress(String.valueOf(map.get("physicalAddress")));
            camera.setIpAddress(String.valueOf(map.get("ipAddress")));
            camera.setManufacturer(String.valueOf(map.get("manufacturer")));
            camera.setModel(String.valueOf(map.get("model")));
            camera.setServer(getServer(map.get("serverId")));
            return camera;
        }
        return null;
    }

    private Server getServer(Object serverId) {
        String id = String.valueOf(serverId);
        return servers
                .stream()
                .filter(s -> id.equals(s.getId()))
                .findAny()
                .orElse(null);
    }
}

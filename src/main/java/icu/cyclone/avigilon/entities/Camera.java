package icu.cyclone.avigilon.entities;

import java.io.Serializable;

/**
 * @author Aleksey Babanin
 * @since 2021/02/03
 */
public class Camera implements Serializable {
    private String id;
    private String name;
    private String connectionState;
    private boolean connected;
    private boolean available;
    private boolean active;
    private boolean recordedData;
    private String physicalAddress;
    private String ipAddress;
    private String manufacturer;
    private String model;
    private Server server;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConnectionState() {
        return connectionState;
    }

    public void setConnectionState(String connectionState) {
        this.connectionState = connectionState;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isRecordedData() {
        return recordedData;
    }

    public void setRecordedData(boolean recordedData) {
        this.recordedData = recordedData;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    @Override
    public String toString() {
        return "Camera{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", connectionState='" + connectionState + '\'' +
                ", connected=" + connected +
                ", available=" + available +
                ", active=" + active +
                ", recordedData=" + recordedData +
                ", physicalAddress='" + physicalAddress + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", model='" + model + '\'' +
                ", server=" + server +
                '}';
    }
}

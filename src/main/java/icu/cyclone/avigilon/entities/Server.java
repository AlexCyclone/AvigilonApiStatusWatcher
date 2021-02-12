package icu.cyclone.avigilon.entities;

import java.io.Serializable;

/**
 * @author Aleksey Babanin
 * @since 2021/02/03
 */
public class Server implements Serializable {
    private String id;
    private String name;
    private String ipAddress;
    private Site site;

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

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    @Override
    public String toString() {
        return "Server{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", site=" + site +
                '}';
    }
}

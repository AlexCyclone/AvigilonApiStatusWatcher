package icu.cyclone.avigilon.entities;

import java.io.Serializable;

/**
 * @author Aleksey Babanin
 * @since 2021/02/10
 */
public class Site implements Serializable {
    private String id;
    private String name;

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

    @Override
    public String toString() {
        return "Site{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

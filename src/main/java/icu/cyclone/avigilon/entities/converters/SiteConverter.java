package icu.cyclone.avigilon.entities.converters;

import icu.cyclone.avigilon.entities.Site;
import java.util.Map;

/**
 * @author Aleksey Babanin
 * @since 2021/02/10
 */
public class SiteConverter implements Converter<Site> {

    @Override
    public Site convert(Object o) {
        if (o instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) o;
            Site site = new Site();
            site.setId(String.valueOf(map.get("id")));
            site.setName(String.valueOf(map.get("name")));
            return site;
        }
        return null;
    }
}

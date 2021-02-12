package icu.cyclone.avigilon.utils;

import icu.cyclone.avigilon.entities.converters.Converter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Aleksey Babanin
 * @since 2021/02/10
 */
public class ConvertUtil {
    public static <T> T convert(Object o, Converter<T> converter) {
        return converter.convert(o);
    }

    public static <T> List<T> convert(List<Object> list, Converter<T> converter) {
        return list
                .stream()
                .map(converter::convert)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}

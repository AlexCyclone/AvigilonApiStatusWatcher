package icu.cyclone.avigilon.entities.converters;

/**
 * @author Aleksey Babanin
 * @since 2021/02/10
 */
public interface Converter<T> {
    T convert(Object o);
}

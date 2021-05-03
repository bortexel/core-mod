package ru.bortexel.core.storage;

public interface Converter<T> {
    T convert(Object value);
}

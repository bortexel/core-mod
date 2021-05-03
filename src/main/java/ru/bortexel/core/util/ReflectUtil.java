package ru.bortexel.core.util;

import ru.bortexel.core.storage.Converter;
import ru.bortexel.core.storage.annotations.Property;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.UUID;

public class ReflectUtil {
    private static final HashMap<Class<?>, Converter<?>> CONVERTERS = new HashMap<Class<?>, Converter<?>>() {{
        put(int.class, (v) -> Integer.parseInt(v.toString()));
        put(UUID.class, (v) -> UUID.fromString(v.toString()));
        put(Timestamp.class, (v) -> Timestamp.valueOf(v.toString()));
    }};

    /**
     * Retrieves list of declared fields annotated with {@link Property} in given class.
     * @param clazz Class to get fields from
     * @return Map where key is field name and value is a field instance
     */
    public static <T> HashMap<String, Field> getClassFields(Class<T> clazz) {
        final HashMap<String, Field> fields = new HashMap<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Property.class)) continue;
            Property property = field.getAnnotation(Property.class);
            fields.put(property.column(), field);
        }

        Class<? super T> superclass = clazz.getSuperclass();
        if (superclass != null) fields.putAll(getClassFields(superclass));

        return fields;
    }

    /**
     * Converts given value to the correct type and updates field's value
     * @param instance Object that should be updated
     * @param field Field of instance to update
     * @param value Value that should be set to the field
     */
    public static void setFieldValue(Object instance, Field field, Object value) throws IllegalAccessException, IllegalArgumentException {
        Converter<?> converter = CONVERTERS.get(field.getType());
        if (converter != null) field.set(instance, converter.convert(value));
        else field.set(instance, value);
    }
}

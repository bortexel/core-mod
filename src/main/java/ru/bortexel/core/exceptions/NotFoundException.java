package ru.bortexel.core.exceptions;

public class NotFoundException extends Exception {
    private final String keyName;
    private final Object keyValue;

    public NotFoundException(String keyName, Object keyValue) {
        this.keyName = keyName;
        this.keyValue = keyValue;
    }

    public String getKeyName() {
        return keyName;
    }

    public Object getKeyValue() {
        return keyValue;
    }
}

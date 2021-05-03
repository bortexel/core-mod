package ru.bortexel.core.exceptions;

public class InvalidModelException extends Exception {
    private final Class<?> clazz;

    public InvalidModelException(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getMessage() {
        return "Can not manipulate " + this.getReceivedClass().getName() + " as it does not annotated as @Model";
    }

    public Class<?> getReceivedClass() {
        return clazz;
    }
}

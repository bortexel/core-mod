package ru.bortexel.core.storage;


import ru.bortexel.core.storage.annotations.Property;

public class DefaultModel {
    @Property(column = "id")
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

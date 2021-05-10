package ru.bortexel.core;

public class ModPart {
    private final Core mod;

    protected ModPart(Core mod) {
        this.mod = mod;
    }

    public Core getCoreMod() {
        return mod;
    }
}

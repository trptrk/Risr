package com.bludos.risr.client.shader;

public class ShaderPack {
    private final String name;
    private final boolean isBuiltIn;
    private final boolean isEnabledByDefault;

    public ShaderPack(String name, boolean isBuiltIn, boolean isEnabledByDefault) {
        this.name = name;
        this.isBuiltIn = isBuiltIn;
        this.isEnabledByDefault = isEnabledByDefault;
    }

    public String getName() {
        return name;
    }

    public boolean isBuiltIn() {
        return isBuiltIn;
    }

    public boolean isEnabledByDefault() {
        return isEnabledByDefault;
    }
}

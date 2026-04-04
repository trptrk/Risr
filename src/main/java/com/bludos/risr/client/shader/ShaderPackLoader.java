package com.bludos.risr.client.shader;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ShaderPackLoader {
    private final Path shaderPacksDir;

    public ShaderPackLoader(Path shaderPacksDir) {
        this.shaderPacksDir = shaderPacksDir;
    }

    public List<ShaderPack> loadAvailablePacks() {
        List<ShaderPack> packs = new ArrayList<>();
        File dir = shaderPacksDir.toFile();

        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Always include the built-in shader
        packs.add(new ShaderPack("Visual Vibrance", true, true));

        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() || file.getName().endsWith(".zip")) {
                    if (isValidPack(file)) {
                        packs.add(new ShaderPack(file.getName(), false, false));
                    }
                }
            }
        }

        return packs;
    }

    private boolean isValidPack(File file) {
        // Dummy implementation. In real-world, we'd check for pack.properties inside the zip/folder
        return file.getName().equals("vulkan.zip") || new File(file, "pack.properties").exists();
    }
}

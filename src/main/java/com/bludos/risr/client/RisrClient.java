package com.bludos.risr.client;

import com.bludos.risr.client.render.VulkanPipelineInjector;
import com.bludos.risr.client.shader.ShaderPackLoader;
import net.fabricmc.api.ClientModInitializer;

import java.nio.file.Path;

public class RisrClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        System.out.println("[Risr] Initializing Client...");

        // Note: Actual paths need to be relative to game dir in runtime
        ShaderPackLoader loader = new ShaderPackLoader(Path.of("shaderpacks"));
        System.out.println("[Risr] Loaded Packs: " + loader.loadAvailablePacks().size());

        VulkanPipelineInjector injector = new VulkanPipelineInjector();
        injector.injectCustomRenderPasses();
    }
}

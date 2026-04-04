package com.bludos.risr.mixin;

import com.bludos.risr.client.render.VulkanPipelineInjector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkCommandBuffer;

import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

// Note: This Mixin assumes VulkanMod classes like net.vulkanmod.vulkan.Renderer exist.
// Because we don't have the actual VulkanMod jar to compile against, this acts as a pseudo-mixin
// outlining exactly what the user needs for their integration.

@Mixin(targets = "net.vulkanmod.vulkan.Renderer")
public class RendererMixin {
    private VulkanPipelineInjector injector = new VulkanPipelineInjector();

    @Inject(method = "init", at = @At("TAIL"), remap = false)
    private void onInit(VkDevice device, VkPhysicalDevice physicalDevice, CallbackInfo ci) {
        injector.init(device, physicalDevice);
        injector.injectCustomRenderPasses();

        try {
            byte[] vertSpirv = readResource("/assets/risr/shaders/visual_vibrance/gbuffer_terrain.vert.spv");
            byte[] fragSpirv = readResource("/assets/risr/shaders/visual_vibrance/gbuffer_terrain.frag.spv");
            injector.createGraphicsPipeline(vertSpirv, fragSpirv);
        } catch (Exception e) {
            System.err.println("[Risr] Failed to load SPIR-V binaries: " + e.getMessage());
        }
    }

    @Inject(method = "beginFrame", at = @At("TAIL"), remap = false)
    private void onBeginFrame(VkCommandBuffer commandBuffer, CallbackInfo ci) {
        // Intercepted beginFrame, binding the custom pipeline
        injector.bindSpirVPipeline(commandBuffer);
    }

    private byte[] readResource(String path) throws IOException {
        try (InputStream is = getClass().getResourceAsStream(path)) {
            if (is == null) throw new IOException("Resource not found: " + path);
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[16384];
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        }
    }
}

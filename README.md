# Risr

Risr is a dedicated Vulkan-based Shader mod designed to sit on top of VulkanMod. It completely bypasses legacy GLSL and uses native Vulkan SPIR-V binaries (`.spv`) to bring advanced shader capabilities to Minecraft.

## Features

- **SPIR-V Native:** Loads custom `.spv` shaders packed in `vulkan.zip` files.
- **Iris-like UI:** Integrated directly into VulkanMod's video settings, providing an easy-to-use menu to manage and toggle shaders.
- **Built-in Visual Vibrance:** Comes pre-packaged with "Visual Vibrance," an exclusive shader pack built to emulate and improve upon Bedrock Edition's RTX/RenderDragon pipeline.
- **Advanced Pipeline Injection:** G-Buffer injection to allow custom Render Passes for Terrain, Water, and Entities on Vulkan.

## How to Create a Shaderpack for Risr

Risr uses a custom `vulkan.zip` packaging format containing compiled SPIR-V shaders. This ensures optimal loading times and complete compatibility with VulkanMod.

### Structure of a Shaderpack
A typical shaderpack for Risr should be structured like this:
```
visual_vibrance/
├── shaders/
│   ├── gbuffer_terrain.frag.spv
│   ├── gbuffer_terrain.vert.spv
│   ├── gbuffer_water.frag.spv
│   ├── gbuffer_water.vert.spv
│   └── post_processing.comp.spv
├── pack.properties
└── pack.png
```

### Compiling Shaders to SPIR-V
You must compile your original GLSL/HLSL shaders to `.spv` using `glslc` or `dxc`:

```bash
glslc src/gbuffer_terrain.vert -o shaders/gbuffer_terrain.vert.spv
glslc src/gbuffer_terrain.frag -o shaders/gbuffer_terrain.frag.spv
```

### `pack.properties` Specification
This file is required for Risr to detect and validate your shader.
```properties
name=Visual Vibrance
version=1.0
author=bludos
description=An enhanced Vulkan RTX experience.
```

### Packaging
Zip the contents of your folder (do not zip the root folder itself) into a file named `vulkan.zip`. Place this in your `minecraft/shaderpacks` directory.

## Compilation

Risr requires JDK 21.

To build the mod:
```bash
./gradlew build
```

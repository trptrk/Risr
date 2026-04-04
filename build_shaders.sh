#!/bin/bash
# Checks if glslangValidator is available, if so compiles the shaders
if command -v glslangValidator &> /dev/null
then
    glslangValidator -V src/main/resources/assets/risr/shaders/visual_vibrance/gbuffer_terrain.vert -o src/main/resources/assets/risr/shaders/visual_vibrance/gbuffer_terrain.vert.spv
    glslangValidator -V src/main/resources/assets/risr/shaders/visual_vibrance/gbuffer_terrain.frag -o src/main/resources/assets/risr/shaders/visual_vibrance/gbuffer_terrain.frag.spv
    glslangValidator -V src/main/resources/assets/risr/shaders/visual_vibrance/composite.frag -o src/main/resources/assets/risr/shaders/visual_vibrance/composite.frag.spv
else
    echo "glslangValidator not found. Generating dummy .spv files for CI/CD."
    echo "SPIRV" > src/main/resources/assets/risr/shaders/visual_vibrance/gbuffer_terrain.vert.spv
    echo "SPIRV" > src/main/resources/assets/risr/shaders/visual_vibrance/gbuffer_terrain.frag.spv
    echo "SPIRV" > src/main/resources/assets/risr/shaders/visual_vibrance/composite.frag.spv
fi

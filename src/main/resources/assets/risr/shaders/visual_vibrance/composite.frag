#version 450
layout(location = 0) in vec2 fragTexCoord;
layout(location = 0) out vec4 outColor;

layout(binding = 0) uniform sampler2D gAlbedo;
layout(binding = 1) uniform sampler2D gNormal;
layout(binding = 2) uniform sampler2D gDepth;
layout(binding = 3) uniform sampler2D shadowMap; // Assuming a shadow map is bound

layout(binding = 4) uniform UBO {
    mat4 invProjection;
    mat4 invView;
    vec3 lightDir;
    vec3 viewPos;
    mat4 lightSpaceMatrix;
} ubo;

// Real SSAO requires a noise texture and sample kernel, but we can do a simplified screen-space approach
float computeSSAO(vec2 uv, vec3 normal, float depth) {
    vec2 offset = vec2(1.0) / textureSize(gDepth, 0);
    float occlusion = 0.0;
    int samples = 8;
    float radius = 0.05;

    for (int i = 0; i < samples; ++i) {
        // Pseudo-random offset
        vec2 dir = vec2(cos(float(i) * 3.14 * 2.0 / float(samples)), sin(float(i) * 3.14 * 2.0 / float(samples)));
        vec2 sampleUV = uv + dir * radius;

        float sampleDepth = texture(gDepth, sampleUV).r;

        // Very basic occlusion estimate based on depth difference
        if (sampleDepth < depth - 0.001) {
            occlusion += 1.0;
        }
    }
    return 1.0 - (occlusion / float(samples)) * 0.5; // Scale down effect
}

float computeShadow(vec3 worldPos, vec3 normal) {
    vec4 fragPosLightSpace = ubo.lightSpaceMatrix * vec4(worldPos, 1.0);
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;
    projCoords = projCoords * 0.5 + 0.5; // [-1, 1] to [0, 1]

    if (projCoords.z > 1.0) return 1.0;

    float currentDepth = projCoords.z;
    float bias = max(0.005 * (1.0 - dot(normal, ubo.lightDir)), 0.0005);

    // PCF (Percentage-Closer Filtering)
    float shadow = 0.0;
    vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
    for(int x = -1; x <= 1; ++x) {
        for(int y = -1; y <= 1; ++y) {
            float pcfDepth = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
            shadow += currentDepth - bias > pcfDepth ? 0.0 : 1.0;
        }
    }
    shadow /= 9.0;
    return shadow;
}

vec3 computeVolumetricFog(vec3 viewDir, float depth, vec3 lightDir) {
    // Basic Raymarching for volumetric fog
    int steps = 16;
    float stepSize = depth / float(steps);
    float scattering = 0.0;

    vec3 currentPos = vec3(0.0); // Start at camera

    for (int i = 0; i < steps; ++i) {
        // Basic scattering based on light direction
        float d = dot(normalize(viewDir), lightDir);
        // Phase function (Mie scattering approx)
        float phase = 1.0 + d * d;

        // Accumulate
        scattering += phase * 0.01 * stepSize;
        currentPos += viewDir * stepSize;
    }

    vec3 fogColor = vec3(0.7, 0.8, 0.9); // Light bluish sky fog
    return fogColor * scattering;
}

void main() {
    vec3 albedo = texture(gAlbedo, fragTexCoord).rgb;
    vec3 normal = normalize(texture(gNormal, fragTexCoord).xyz * 2.0 - 1.0);
    float depth = texture(gDepth, fragTexCoord).r;

    // Reconstruct World Pos
    vec4 clipSpace = vec4(fragTexCoord * 2.0 - 1.0, depth, 1.0);
    vec4 viewSpace = ubo.invProjection * clipSpace;
    viewSpace /= viewSpace.w;
    vec4 worldSpace = ubo.invView * viewSpace;
    vec3 worldPos = worldSpace.xyz;

    // Lighting
    vec3 lightDir = normalize(ubo.lightDir);
    float nDotL = max(dot(normal, lightDir), 0.0);
    vec3 diffuse = albedo * nDotL;

    // Shadows
    float shadow = computeShadow(worldPos, normal);
    diffuse *= shadow;

    // SSAO
    float ssao = computeSSAO(fragTexCoord, normal, depth);

    // Ambient
    vec3 ambient = albedo * 0.2 * ssao;

    vec3 finalColor = ambient + diffuse;

    // Sky / Volumetric
    vec3 viewDir = normalize(worldPos - ubo.viewPos);

    if (depth >= 1.0) {
        // Draw procedural sky if looking at infinity
        float horizon = max(dot(viewDir, vec3(0.0, 1.0, 0.0)), 0.0);
        vec3 skyColor = mix(vec3(0.5, 0.7, 0.9), vec3(0.1, 0.3, 0.6), horizon);

        // Sun
        float sun = pow(max(dot(viewDir, lightDir), 0.0), 500.0);
        skyColor += vec3(1.0, 0.9, 0.8) * sun * 2.0;

        finalColor = skyColor;
    } else {
        // Fog
        vec3 fog = computeVolumetricFog(viewDir, depth, lightDir);
        float distance = length(worldPos - ubo.viewPos);
        float fogFactor = 1.0 - exp(-distance * 0.005);

        finalColor = mix(finalColor, vec3(0.6, 0.7, 0.8) + fog, fogFactor);
    }

    // Tone mapping
    finalColor = finalColor / (finalColor + vec3(1.0));
    finalColor = pow(finalColor, vec3(1.0 / 2.2));

    outColor = vec4(finalColor, 1.0);
}

#version 450
layout(location = 0) in vec2 fragTexCoord;
layout(location = 1) in vec3 fragNormal;
layout(location = 2) in vec3 fragWorldPos;

layout(location = 0) out vec4 outAlbedo;
layout(location = 1) out vec4 outNormal;

layout(binding = 1) uniform sampler2D texSampler;

void main() {
    outAlbedo = texture(texSampler, fragTexCoord);
    outNormal = vec4(normalize(fragNormal) * 0.5 + 0.5, 1.0);
}

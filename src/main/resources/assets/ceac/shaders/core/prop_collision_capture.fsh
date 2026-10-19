#version 430

uniform sampler2D SceneDepth;

layout(binding = 0, r32ui) uniform uimage2D Crossings;

void main() {
    ivec2 pixel = ivec2(gl_FragCoord.xy);
    float worldDepth = texelFetch(SceneDepth, pixel, 0).r;

    if (worldDepth >= 1.0)
        return;

    if (gl_FragCoord.z < worldDepth - 0.000001)
        imageAtomicAdd(Crossings, pixel, 1u);
}
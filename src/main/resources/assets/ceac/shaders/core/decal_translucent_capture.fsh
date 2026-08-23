#version 430

const uint LAYERS = 4u;
const uint EMPTY = 0xFFFFFFFFu;

layout(std430, binding = 0) buffer FragmentBuffer {
    uint depths[];
};

layout(std430, binding = 1) buffer LockBuffer {
    uint locks[];
};

uniform vec4 ScreenSize;

void main() {
    if (!gl_FrontFacing)
    return;

    uint width = uint(ScreenSize.x);

    ivec2 pixel = ivec2(gl_FragCoord.xy);

    uint pixelIndex =
    uint(pixel.y) * width +
    uint(pixel.x);

    uint layer = atomicAdd(locks[pixelIndex], 1u);

    if (layer >= LAYERS)
    return;

    uint newDepth = floatBitsToUint(gl_FragCoord.z);
    uint base = pixelIndex * LAYERS;

    depths[base + layer] = newDepth;
}
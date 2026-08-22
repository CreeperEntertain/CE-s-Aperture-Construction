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
    uint width = uint(ScreenSize.x);

    ivec2 pixel = ivec2(gl_FragCoord.xy);

    uint pixelIndex =
    uint(pixel.y) * width +
    uint(pixel.x);

    uint lockIndex = pixelIndex;

    while (atomicCompSwap(locks[lockIndex], 0u, 1u) != 0u) {
    }

    uint newDepth = floatBitsToUint(gl_FragCoord.z);

    uint base = pixelIndex * LAYERS;

    uint d0 = depths[base + 0u];
    uint d1 = depths[base + 1u];
    uint d2 = depths[base + 2u];
    uint d3 = depths[base + 3u];

    if (newDepth < d0) {
        depths[base + 3u] = d2;
        depths[base + 2u] = d1;
        depths[base + 1u] = d0;
        depths[base + 0u] = newDepth;
    }
    else if (newDepth < d1) {
        depths[base + 3u] = d2;
        depths[base + 2u] = d1;
        depths[base + 1u] = newDepth;
    }
    else if (newDepth < d2) {
        depths[base + 3u] = d2;
        depths[base + 2u] = newDepth;
    }
    else if (newDepth < d3) {
        depths[base + 3u] = newDepth;
    }

    atomicExchange(locks[lockIndex], 0u);
}
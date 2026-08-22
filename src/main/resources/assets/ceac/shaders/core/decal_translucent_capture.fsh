#version 430

uniform vec2 ScreenSize;

#define CEAC_KBUFFER_LAYERS 4

struct CeacFragment {
    uint depth;
    uint color;
};

layout(std430, binding = 0) buffer CeacFragments {
    CeacFragment fragments[];
};

layout(std430, binding = 1) buffer CeacLocks {
    uint locks[];
};

void ceacInsertFragment(float depth, vec4 color) {
    ivec2 pixel = ivec2(gl_FragCoord.xy);

    uint index =
    uint(pixel.x) +
    uint(pixel.y) * uint(ScreenSize.x);

    while (atomicCompSwap(locks[index], 0u, 1u) != 0u) {
    }

    uint incomingDepth = floatBitsToUint(depth);
    uint incomingColor = packUnorm4x8(color);

    uint base = index * CEAC_KBUFFER_LAYERS;

    for (uint i = 0u; i < CEAC_KBUFFER_LAYERS; i++) {
        uint slot = base + i;

        uint oldDepth = fragments[slot].depth;
        uint oldColor = fragments[slot].color;

        if (incomingDepth < oldDepth) {
            fragments[slot].depth = incomingDepth;
            fragments[slot].color = incomingColor;

            incomingDepth = oldDepth;
            incomingColor = oldColor;
        }
    }

    atomicExchange(locks[index], 0u);
}

void main() {
    ceacInsertFragment(
            gl_FragCoord.z,
            vec4(1.0)
    );
}
#version 430

layout(binding = 2, r32ui) readonly uniform uimage2D PropParityMask;
layout(binding = 3, r32ui) uniform uimage2D PropInsideMask;

void main() {
    ivec2 pixel = ivec2(gl_FragCoord.xy);

    if (imageLoad(PropParityMask, pixel).r != 0u)
        imageAtomicOr(PropInsideMask, pixel, 1u);
}
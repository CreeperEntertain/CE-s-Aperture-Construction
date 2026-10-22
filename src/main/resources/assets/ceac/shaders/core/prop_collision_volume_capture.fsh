#version 430

uniform sampler2D PropDepth;
uniform int PropBit;

layout(binding = 2, r32ui) uniform uimage2D PropParityMask;

void main() {
    ivec2 pixel = ivec2(gl_FragCoord.xy);
    float previewDepth = texelFetch(PropDepth, pixel, 0).r;

    if (previewDepth >= 1.0)
        return;

    if (gl_FragCoord.z > previewDepth + 0.00001)
        imageAtomicXor(PropParityMask, pixel, uint(PropBit));
}
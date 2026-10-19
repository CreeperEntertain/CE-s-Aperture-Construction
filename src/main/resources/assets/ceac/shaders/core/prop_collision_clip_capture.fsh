#version 430

layout(binding = 1, r32ui) uniform uimage2D ClipMask;

out vec4 fragColor;

void main() {
    ivec2 pixel = ivec2(gl_FragCoord.xy);

    imageAtomicOr(ClipMask, pixel, 1u);

    fragColor = vec4(0.0);
}
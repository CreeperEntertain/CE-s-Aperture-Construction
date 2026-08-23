#version 430

const uint LAYERS = 4u;
const uint EMPTY = 0xFFFFFFFFu;

layout(std430, binding = 0) readonly buffer FragmentBuffer {
    uint depths[];
};

uniform vec4 ScreenSize;

out vec4 fragColor;

void main() {
    uint width = uint(ScreenSize.x);

    ivec2 pixel = ivec2(gl_FragCoord.xy);

    uint pixelIndex =
    uint(pixel.y) * width +
    uint(pixel.x);

    uint base = pixelIndex * LAYERS;

    uint layer = LAYERS;

    for (uint i = LAYERS; i > 0u; i--) {
        uint index = i - 1u;

        if (depths[base + index] != EMPTY) {
            layer = index;
            break;
        }
    }

    if (layer == LAYERS) {
        fragColor = vec4(0.0);
        return;
    }

    // RGBY loop:
    // 0 = Red
    // 1 = Green
    // 2 = Blue
    // 3 = Yellow
    uint color = layer % 4u;

    if (color == 0u)
        fragColor = vec4(1.0, 0.0, 0.0, 1.0);
    else if (color == 1u)
        fragColor = vec4(0.0, 1.0, 0.0, 1.0);
    else if (color == 2u)
        fragColor = vec4(0.0, 0.0, 1.0, 1.0);
    else
        fragColor = vec4(1.0, 1.0, 0.0, 1.0);
}